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

+ (id<OpenInstallDelegate> _Nonnull)allocWithZone:(NSZone *_Nullable)zone;

+ (void)handLinkURL:(NSURL *)url;

+ (void)continueUserActivity:(NSUserActivity *)userActivity;
@end
