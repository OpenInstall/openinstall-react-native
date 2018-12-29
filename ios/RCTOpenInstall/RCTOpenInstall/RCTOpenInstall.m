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
        NSDictionary *dic = @{@"channel":appData.channelCode?:@"",@"data":appData.data?:@""};
        NSArray *params = @[dic];
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
    NSDictionary *params = @{@"channel":appData.channelCode?:@"",@"data":appData.data?:@""};
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


@end
