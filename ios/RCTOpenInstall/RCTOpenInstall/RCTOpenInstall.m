//
//  RCTOpenInstall.m
//  RCTOpenInstall
//
//  Created by cooper on 2018/11/26.
//  Copyright © 2018年 openinstall. All rights reserved.
//

#import "RCTOpenInstall.h"

#if __has_include(<React/RCTBridge.h>)
#import <React/RCTEventDispatcher.h>
#import <React/RCTRootView.h>
#import <React/RCTBridge.h>
#import <React/RCTLog.h>

#elif __has_include("RCTBridge.h")
#import "RCTEventDispatcher.h"
#import "RCTRootView.h"
#import "RCTBridge.h"
#import "RCTLog.h"

#elif __has_include("React/RCTBridge.h")
#import "React/RCTEventDispatcher.h"
#import "React/RCTRootView.h"
#import "React/RCTBridge.h"
#import "React/RCTLog.h"
#endif

#define OpeninstallWakeupCallBack @"OpeninstallWakeupCallBack"

@interface RCTOpenInstall ()<OpenInstallDelegate>
@property (nonatomic, strong)NSDictionary *wakeUpParams;
@property (nonatomic, assign)BOOL notFirstLoad;
@end

@implementation RCTOpenInstall

@synthesize bridge = _bridge;

RCT_EXPORT_MODULE(OpeninstallModule);

+ (id)allocWithZone:(NSZone *)zone {
    static RCTOpenInstall *sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [super allocWithZone:zone];
        sharedInstance.wakeUpParams = [[NSDictionary alloc] init];
    });
    return sharedInstance;
}

RCT_EXPORT_METHOD(getInstall:(int)s completion:(RCTResponseSenderBlock)callback)
{
    
    NSTimeInterval time = 10.0f;
    if (s>0) {
        time = s;
    }
    [[OpenInstallSDK defaultManager] getInstallParmsWithTimeoutInterval:time completed:^(OpeninstallData * _Nullable appData) {
        
        if (!appData.data&&!appData.channelCode) {
            NSArray *params = @[[NSNull null]];
            callback(params);
            return;
        }
        NSString *channelID = @"";
        NSString *datas = @"";
        if (appData.data) {
            datas = [self jsonStringWithObject:appData.data];
        }
        if (appData.channelCode) {
            channelID = appData.channelCode;
        }
        NSArray *params = @[@{@"channel":channelID,@"data":datas}];
        callback(params);
    }];
}

- (void)getWakeUpParams:(OpeninstallData *)appData{
    if (!appData.data&&!appData.channelCode) {
        if (self.bridge) {
            [self.bridge.eventDispatcher sendAppEventWithName:OpeninstallWakeupCallBack body:nil];
        }
        return;
    }
    NSString *channelID = @"";
    NSString *datas = @"";
    if (appData.data) {
        datas = [self jsonStringWithObject:appData.data];
    }
    if (appData.channelCode) {
        channelID = appData.channelCode;
    }
    NSDictionary *params = @{@"channel":channelID,@"data":datas};
    if (self.bridge) {
        [self.bridge.eventDispatcher sendAppEventWithName:OpeninstallWakeupCallBack body:params];
    }else{
        @synchronized(self){
            self.wakeUpParams = params;
        }
    }
}

RCT_EXPORT_METHOD(getWakeUp:(RCTResponseSenderBlock)callback)
{
    if (!self.notFirstLoad) {
        if (self.wakeUpParams.count != 0) {
            NSArray *params = @[self.wakeUpParams];
            callback(params);
        }else{
            callback(@[[NSNull null]]);
        }
        self.notFirstLoad = YES;
    }
}

RCT_EXPORT_METHOD(reportRegister)
{
    [OpenInstallSDK reportRegister];
}

RCT_EXPORT_METHOD(reportEffectPoint:(NSString *)effectID effectValue:(NSInteger)effectValue)
{
    [[OpenInstallSDK defaultManager] reportEffectPoint:effectID effectValue:effectValue];
}

- (NSString *)jsonStringWithObject:(id)jsonObject{
    // 将字典或者数组转化为JSON串
    NSError *error = nil;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:jsonObject
                                                       options:NSJSONWritingPrettyPrinted
                                                         error:&error];
    
    NSString *jsonString = [[NSString alloc] initWithData:jsonData
                                                 encoding:NSUTF8StringEncoding];
    
    if ([jsonString length] > 0 && error == nil){
        jsonString = [jsonString stringByReplacingOccurrencesOfString:@"\n" withString:@""];
        return jsonString;
    }else{
        return @"";
    }
}

@end
