### iOS 手动集成方式

- 在React Native < 0.60版本下， `react-native link` 之后，打开 iOS 工程  
- 在React Native >= 0.60版本下，通过pod安装插件后，打开iOS工程  

也可不执行 `react-native link` 或cocoapod安装，通过手动拖拽openinstall-react-native插件 `RCTOpenInstall.xcodeproj` 到xcode工程中，这部分可参考[官方文档](https://reactnative.cn/docs/linking-libraries-ios/)或[facebook英文文档](https://facebook.github.io/react-native/docs/linking-libraries-ios)

#### 1 相关配置

##### （1）初始化配置
在 `Info.plist` 文件中配置 appKey 键值对，如下：
``` xml
<key>com.openinstall.APP_KEY</key>
<string>从openinstall官网后台获取应用的appkey</string>
```
##### （2）universal links配置（iOS9以后推荐使用）

对于iOS，为确保能正常跳转，AppID必须开启Associated Domains功能，请到[苹果开发者网站](https://developer.apple.com)，选择Certificate, Identifiers & Profiles，选择相应的AppID，开启Associated Domains。注意：当AppID重新编辑过之后，需要更新相应的mobileprovision证书。(图文配置步骤请看[iOS集成指南](https://www.openinstall.io/doc/ios_sdk.html))，如果已经开启过Associated Domains功能，进行下面操作：

- 在左侧导航器中点击您的项目
- 选择 `Capabilities` 标签
- 打开 `Associated Domains` 开关
- 添加 openinstall 官网后台中应用对应的关联域名（openinstall应用控制台->iOS集成->iOS应用配置->关联域名(Associated Domains)）

##### （3）scheme配置
- `scheme` 的值请在openinstall控制台获取（openinstall应用控制台->iOS集成->iOS应用配置）
在 `Info.plist` 文件中，在 `CFBundleURLTypes` 数组中添加应用对应的 `scheme`，或者在工程“TARGETS-Info-URL Types”里快速添加，图文配置请看[iOS集成指南](https://www.openinstall.io/doc/ios_sdk.html)  
（scheme的值详细获取位置：openinstall应用控制台->iOS集成->iOS应用配置）

``` xml
 <key>CFBundleURLTypes</key>
 <array>
  <dict>
    <key>CFBundleTypeRole</key>
    <string>Editor</string>
    <key>CFBundleURLName</key>
    <string>openinstall</string>
    <key>CFBundleURLSchemes</key>
    <array>
      <string>"从openinstall官网后台获取应用的scheme"</string>
    </array>
  </dict>
 </array>
```

##### 注意：

- 在 iOS 工程中如果找不到头文件可能要在 TARGETS-> BUILD SETTINGS -> Search Paths -> Header Search Paths 添加如下如路径：
````
$(SRCROOT)/../node_modules/openinstall-react-native/ios/RCTOpenInstall
````

#### 2 相关代码

（1）AppDelegate.h 中添加如下代码，导入头文件
```
#import <RCTOpenInstall/RCTOpenInstall.h> //有些老版本RN可能需要双引号引入#import "RCTOpenInstall/RCTOpenInstall.h"

//通过cocoapod安装插件头文件路径不一样，如下
#import <openinstall-react-native/RCTOpenInstall.h>
```

（2）初始化sdk的代码
AppDelegate.m 的 `didFinishLaunchingWithOptions` 方法里面添加如下代码：
```
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
 //初始化openinstall sdk
 [OpenInstallSDK initWithDelegate:[RCTOpenInstall allocWithZone:nil]];

 return YES;
}
```

（3）scheme相关代码  
AppDelegate.m 里面添加如下代码：
```
//iOS9以上，会优先走这个方法
- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url options:(NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options{
 //openURL1
 [OpenInstallSDK handLinkURL:url];
 return YES;
}
//适用目前所有iOS版本
- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation{
 //openURL2
 [OpenInstallSDK handLinkURL:url];
 return YES;
}
```

（4）universal link相关代码  
AppDelegate.m 里面添加如下代码：
```
- (BOOL)application:(UIApplication *)application continueUserActivity:(NSUserActivity *)userActivity restorationHandler:(void (^)(NSArray * _Nullable))restorationHandler{
 //univeral link
 [OpenInstallSDK continueUserActivity:userActivity];
 return YES;
}
```

**openinstall完全兼容微信openSDK1.8.6以上版本的通用链接跳转功能，注意微信SDK初始化方法中，传入正确格式的universal link链接：**  

``` objc
//your_wxAppID从微信后台获取，yourAppkey从openinstall后台获取
[WXApi registerApp:@"your_wxAppID" universalLink:@"https://yourAppkey.openinstall.io/ulink/"];
```

微信开放平台后台Universal links配置，要和上面代码中的保持一致  

![微信后台配置](res/wexinUL.jpg)

- 微信SDK更新参考[微信开放平台更新文档](https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/iOS.html)  

