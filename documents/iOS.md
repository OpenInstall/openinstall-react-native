### iOS 手动集成方式

在 `react-native link` 之后，打开 iOS 工程。

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

#### 2 相关代码

（1）AppDelegate.h 中添加如下代码，导入头文件
```
#import <RCTOpenInstall/RCTOpenInstall.h>
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
 //scheme1
 [OpenInstallSDK handLinkURL:url];
 return YES;
}
//适用目前所有iOS版本
- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation{
 //scheme2
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
