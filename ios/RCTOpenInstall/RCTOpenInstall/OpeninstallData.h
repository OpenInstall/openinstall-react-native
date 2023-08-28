//
//  OpeninstallData.h
//  OpenInstallSDK
//
//  Created by cooper on 2018/4/17.
//  Copyright © 2018年 cooper. All rights reserved.
//

#import <Foundation/Foundation.h>

//added in v2.7.0 用于分享统计接口中的分享平台（sharePlatform）
typedef NSString *OP_SharePlatform NS_STRING_ENUM;

/**
 * 微信好友
 */
extern OP_SharePlatform const OP_SharePlatform_WechatSession;
/**
 * 微信朋友圈
 */
extern OP_SharePlatform const OP_SharePlatform_WechatTimeline;
/**
 * 微信收藏
 */
extern OP_SharePlatform const OP_SharePlatform_WechatFavorite;
/**
 * 企业微信，国际版WeCom，原名WechatWork
 */
extern OP_SharePlatform const OP_SharePlatform_WeCom;
/**
 * QQ好友
 */
extern OP_SharePlatform const OP_SharePlatform_QQ;
/**
 * QQ空间
 */
extern OP_SharePlatform const OP_SharePlatform_Qzone;
/**
 * 新浪微博
 */
extern OP_SharePlatform const OP_SharePlatform_Sina;
/**
 * 腾讯微博
 */
extern OP_SharePlatform const OP_SharePlatform_TencentWb;
/**
 * 腾讯Tim
 */
extern OP_SharePlatform const OP_SharePlatform_TencentTim;
/**
 * 支付宝好友
 */
extern OP_SharePlatform const OP_SharePlatform_APSession;
/**
 * 钉钉
 */
extern OP_SharePlatform const OP_SharePlatform_DingDing;
/**
 * 抖音国内版
 */
extern OP_SharePlatform const OP_SharePlatform_DouYin;
/**
 * 抖音海外版（TikTok）
 */
extern OP_SharePlatform const OP_SharePlatform_TikTok;
/**
 * 快手
 */
extern OP_SharePlatform const OP_SharePlatform_Kuaishou;
/**
 * 快手国际版（Kwai）
 */
extern OP_SharePlatform const OP_SharePlatform_Kwai;
/**
 * 西瓜视频
 */
extern OP_SharePlatform const OP_SharePlatform_WatermelonVideo;
/**
 * 西瓜视频国际版（BuzzVideo）
 */
extern OP_SharePlatform const OP_SharePlatform_BuzzVideo;
/**
 * 人人网
 */
extern OP_SharePlatform const OP_SharePlatform_Renren;
/**
 * 豆瓣
 */
extern OP_SharePlatform const OP_SharePlatform_Douban;
/**
 * 邮箱
 */
extern OP_SharePlatform const OP_SharePlatform_Email;
/**
 * 短信
 */
extern OP_SharePlatform const OP_SharePlatform_Sms;
/**
 * Facebook
 */
extern OP_SharePlatform const OP_SharePlatform_Facebook;
/**
 * Facebook Messenger
 */
extern OP_SharePlatform const OP_SharePlatform_FacebookMessenger;
/**
 * Facebook账户系统
 */
extern OP_SharePlatform const OP_SharePlatform_FacebookAccount;
/**
 * 推特（Twitter）
 */
extern OP_SharePlatform const OP_SharePlatform_Twitter;
/**
 * Instragram
 */
extern OP_SharePlatform const OP_SharePlatform_Instagram;
/**
 * Whatsapp
 */
extern OP_SharePlatform const OP_SharePlatform_Whatsapp;
/**
 * youtube
 */
extern OP_SharePlatform const OP_SharePlatform_Youtube;
/**
 * SnapChat
 */
extern OP_SharePlatform const OP_SharePlatform_SnapChat;

/**
 * 易信好友
 */
extern OP_SharePlatform const OP_SharePlatform_YXSession;
/**
 * 易信朋友圈
 */
extern OP_SharePlatform const OP_SharePlatform_YXTimeline;
/**
 * 易信收藏夹
 */
extern OP_SharePlatform const OP_SharePlatform_YXFavorite;
/**
 * 明道
 */
extern OP_SharePlatform const OP_SharePlatform_MingDao;
/**
 * 来往好友
 */
extern OP_SharePlatform const OP_SharePlatform_LWSession;
/**
 * 来往朋友圈
 */
extern OP_SharePlatform const OP_SharePlatform_LWTimeline;
/**
 * 分享到Line
 */
extern OP_SharePlatform const OP_SharePlatform_Line;
/**
 * 领英
 */
extern OP_SharePlatform const OP_SharePlatform_Linkedin;
/**
 * Reddit
 */
extern OP_SharePlatform const OP_SharePlatform_Reddit;
/**
 * Tumblr
 */
extern OP_SharePlatform const OP_SharePlatform_Tumblr;
/**
 * Pinterest
 */
extern OP_SharePlatform const OP_SharePlatform_Pinterest;
/**
 * Kakao Talk
 */
extern OP_SharePlatform const OP_SharePlatform_KakaoTalk;
/**
 * Kakao story
 */
extern OP_SharePlatform const OP_SharePlatform_KakaoStory;
/**
 * Flickr
 */
extern OP_SharePlatform const OP_SharePlatform_Flickr;
/**
 * 有道云笔记
 */
extern OP_SharePlatform const OP_SharePlatform_YouDaoNote;
/**
 * 印象笔记
 */
extern OP_SharePlatform const OP_SharePlatform_YinxiangNote;
/**
 * 印象笔记国际版
 */
extern OP_SharePlatform const OP_SharePlatform_EverNote;
/**
 * google+
 */
extern OP_SharePlatform const OP_SharePlatform_googlePlus;
/**
 *  Pocket
 */
extern OP_SharePlatform const OP_SharePlatform_Pocket;
/**
 *  dropbox
 */
extern OP_SharePlatform const OP_SharePlatform_dropbox;
/**
 *  vkontakte
 */
extern OP_SharePlatform const OP_SharePlatform_vkontakte;
/**
 * Instapaper
 */
extern OP_SharePlatform const OP_SharePlatform_Instapaper;
/**
 * Oasis
 */
extern OP_SharePlatform const OP_SharePlatform_Oasis;
/**
 * Apple
 */
extern OP_SharePlatform const OP_SharePlatform_AppleAccount;
/**
 * copy
 */
extern OP_SharePlatform const OP_SharePlatform_Copy;
/**
 *  其它平台
 */
extern OP_SharePlatform const OP_SharePlatform_Other;


//added in v2.7.0 用于安装参数返回时的超时判断
typedef NS_ENUM(NSUInteger,OP_Codes) {
    OPCode_normal = 0,//初始化结束，并返回参数，自然安装下参数为空
    OPCode_timeout = 1,//获取参数超时，可在合适时机再去获取（可设置全局标识）
};

extern NSString *const OP_Idfa_Id;//用于第三方广告平台统计
extern NSString *const OP_ASA_Token;//用于苹果ASA搜索广告
extern NSString *const OP_ASA_isDev;//added in v2.5.6 用于ASA debug测试，正式环境下“不要”设置为YES

@interface OpeninstallData : NSObject<NSCopying>

- (instancetype)initWithData:(NSDictionary *)data
                 channelCode:(NSString *)channelCode;

@property (nonatomic,strong) NSDictionary *data;//动态参数
@property (nonatomic,copy) NSString *channelCode;//渠道编号
@property (nonatomic,assign) OP_Codes opCode;//返回码，用于安装参数返回时的超时判断 (added in v2.7.0)

@end
