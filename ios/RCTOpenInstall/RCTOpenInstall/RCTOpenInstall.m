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

@interface RCTOpenInstall ()
@property (nonatomic, strong)NSDictionary *wakeUpParams;
@property (nonatomic, assign)BOOL wakeupStat;
@property (nonatomic, assign)BOOL initStat;
@property (nonatomic, strong)NSURL *handleURL;
@property (nonatomic, strong)NSUserActivity *userActivity;
@end

@implementation RCTOpenInstall

@synthesize bridge = _bridge;

RCT_EXPORT_MODULE(OpeninstallModule);

static RCTOpenInstall *sharedInstance = nil;
+ (id)allocWithZone:(NSZone *)zone {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [super allocWithZone:zone];
        sharedInstance.wakeUpParams = [[NSDictionary alloc] init];
    });
    return sharedInstance;
}

+ (void)initOpenInstall:(NSString *)adid{
    [RCTOpenInstall allocWithZone:nil];
    if (!sharedInstance.initStat) {
        sharedInstance.initStat = YES;
        [OpenInstallSDK initWithDelegate:sharedInstance];
        [RCTOpenInstall check];
    }
}

RCT_EXPORT_METHOD(initSDK:(NSString *)adid)
{
    [RCTOpenInstall initOpenInstall:adid];
}

RCT_EXPORT_METHOD(getInstall:(int)s completion:(RCTResponseSenderBlock)callback)
{
    [RCTOpenInstall initOpenInstall:@""];
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
//            [self.bridge.eventDispatcher sendAppEventWithName:OpeninstallWakeupCallBack body:nil];
            [self.bridge enqueueJSCall:@"RCTDeviceEventEmitter" method:@"emit" args:@[OpeninstallWakeupCallBack] completion:NULL];
        }
        return;
    }
    NSDictionary *params = @{@"channel":appData.channelCode?:@"",@"data":appData.data?:@""};
    if (self.bridge) {
//        [self.bridge.eventDispatcher sendAppEventWithName:OpeninstallWakeupCallBack body:params];
        [self.bridge enqueueJSCall:@"RCTDeviceEventEmitter" method:@"emit" args:@[OpeninstallWakeupCallBack,params] completion:NULL];
    }else{
        @synchronized(self){
            self.wakeUpParams = params;
        }
    }
}

RCT_EXPORT_METHOD(getWakeUp:(RCTResponseSenderBlock)callback)
{
    [RCTOpenInstall allocWithZone:nil];
    if (!self.wakeupStat) {
        if (self.wakeUpParams.count != 0) {
            NSArray *params = @[self.wakeUpParams];
            callback(params);
        }else{
            callback(@[[NSNull null]]);
        }
        self.wakeupStat = YES;
    }
}

RCT_EXPORT_METHOD(reportRegister)
{
    [RCTOpenInstall initOpenInstall:@""];
    [OpenInstallSDK reportRegister];
}

RCT_EXPORT_METHOD(reportEffectPoint:(NSString *)effectID effectValue:(NSInteger)effectValue)
{
    [RCTOpenInstall initOpenInstall:@""];
    [[OpenInstallSDK defaultManager] reportEffectPoint:effectID effectValue:effectValue];
}


+ (void)handLinkURL:(NSURL *)url{
    [RCTOpenInstall wakeupParamStored:url];
}

+ (void)continueUserActivity:(NSUserActivity *)userActivity{
    [RCTOpenInstall wakeupParamStored:userActivity];
}

+ (void)wakeupParamStored:(id)handle{
    [RCTOpenInstall allocWithZone:nil];
    if (sharedInstance.initStat) {
        if ([handle isKindOfClass:[NSURL class]]) {
            [OpenInstallSDK handLinkURL:(NSURL *)handle];
        }else{
            [OpenInstallSDK continueUserActivity:(NSUserActivity *)handle];
        }
    }else{
        if ([handle isKindOfClass:[NSURL class]]) {
            sharedInstance.handleURL = (NSURL *)handle;
        }else{
            sharedInstance.userActivity = (NSUserActivity *)handle;
        }
    }
}

+ (void)check{
    if (sharedInstance.handleURL) {
        [OpenInstallSDK handLinkURL:sharedInstance.handleURL];
        sharedInstance.handleURL = nil;
    }
    if (sharedInstance.userActivity) {
        [OpenInstallSDK continueUserActivity:sharedInstance.userActivity];
        sharedInstance.userActivity = nil;
    }
}
@end
