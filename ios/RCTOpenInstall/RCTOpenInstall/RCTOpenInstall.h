//
//  RCTOpenInstall.h
//  RCTOpenInstall
//
//  Created by cooper on 2018/11/26.
//  Copyright © 2018年 openinstall. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "OpenInstallSDK.h"

#if __has_include(<React/RCTBridgeModule.h>)
#import <React/RCTBridgeModule.h>
#elif __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#elif __has_include("React/RCTBridgeModule.h")
#import "React/RCTBridgeModule.h"
#endif


//#define OpenInstallWakeUpCallBackNotification @"OpenInstallWakeUpCallBackNotification"

@interface RCTOpenInstall : NSObject<RCTBridgeModule,OpenInstallDelegate>

@property (assign, nonatomic) BOOL adEnable;//必要，是否开启广告平台统计功能
@property (assign, nonatomic) BOOL ASAEnable;//必要，是否开启苹果ASA功能
@property (assign, nonatomic) BOOL ASADebug;//可选，ASA测试debug模式，注意：正式环境中请务必关闭
@property (copy, nonatomic) NSString *idfaStr;//可选，通过其它插件获取的idfa字符串一般格式为xxxx-xxxx-xxxx-xxxx
@property (copy, nonatomic) NSString *caid1;
@property (copy, nonatomic) NSString *caid2;

@property (copy, nonatomic) NSString *ASA;


+ (id)shareInstance;

+ (void)handLinkURL:(NSURL *)url;

+ (void)continueUserActivity:(NSUserActivity *)userActivity;
@end
