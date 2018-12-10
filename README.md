# Openinstall React Native Plugin
openinstall-react-native 是openinstall官方开发的 React Native 插件，使用该插件可以方便快速地集成openinstall相关功能。


# 使用 npm 安装插件

在您的项目根目录下执行

```
npm install openinstall-react-native --save
```
```
react-native link
```
- link的时候如果出现 `Error: Cannot find module 'asap/raw'` 则先执行 `npm install` 再 `react-native link` 就好了

## 一、自动集成方式  
（1）使用自动集成脚本集成代码和部分配置
```
npm run openinstall <yourAppKey> <yourScheme>
```
- yourAppKey指的是你在openinstall官方账号后台，创建应用后分配的AppKey
- yourScheme指的是你在openinstall官方账号后台，创建应用后分配的scheme  

举例：
```
npm run openinstall e7iomw rc8tey
```
（2）xcode配置（只对iOS）  
在 iOS 工程中，如果要使用universal links(通用链接)的拉起功能，需要开启 Associated Domains功能，请到[苹果开发者网站](https://developer.apple.com)，选择Certificate, Identifiers & Profiles，选择相应的AppID，开启Associated Domains。注意：当AppID重新编辑过之后，需要更新相应的mobileprovision证书。(详细配置步骤请看[openinstall官网](https://www.openinstall.io)后台文档，universal link关联域名从后台获取)，如果已经开启过Associated Domains功能，进行下面操作：
- 在左侧导航器中点击您的项目
- 选择 `Capabilities` 标签
- 打开 `Associated Domains` 开关
- 添加 openinstall 官网后台中应用对应的关联域名（iOS集成->iOS应用配置->关联域名(Associated Domains)）

#### 注意：

- 如果在执行自动集成脚本时发生错误，请使用文档后面提供的手动集成方式。

- 在 iOS 工程中如果找不到头文件可能要在 TARGETS-> BUILD SETTINGS -> Search Paths -> Header Search Paths 添加如下如路径：
````
$(SRCROOT)/../node_modules/openinstall-react-native/ios/RCTOpenInstall
````

## 使用指南
### 1 快速下载
如果只需要快速下载功能，无需其它功能（携带参数安装、渠道统计、一键拉起），完成sdk初始化即可（自动集成方式或手动集成方式）

### 2 一键拉起
#### 导入插件
请react native端入口js文件中（例如App.js文件）导入：
```
import OpeninstallModule from 'openinstall-react-native'
```
#### 获取拉起参数
通常在 `componentDidMount` 中监听
```
componentDidMount() {
  //该方法用于监听app通过univeral link或scheme拉起后获取唤醒参数
  this.receiveWakeupListener = map => {
     if (map) {
	   //do your work here
     }        
   Alert.alert('拉起回调',JSON.stringify(map)) 
  } 
  OpeninstallModule.addWakeUpListener(this.receiveWakeupListener)  
}

componentWillUnMount() {
  OpeninstallModule.removeWakeUpListener(this.receiveWakeupListener)//移除监听
}
```
- 第二个函数返回的是map或字典，包含动态安装参数（data）和渠道参数（channel），注意：只有通过渠道二维码或链接安装app后，才会有渠道参数
- 如果动态安装参数（data）和渠道参数（channel）同时为空，则map返回null

### 3 携带参数安装 <span style="margin-left: 5px;display: inline-block;background: red;color: #fff;border-radius: 3px;padding: 2px 3px;font-size: 12px;">高级版功能</span>
在需要获取安装参数的位置，导入插件：
```
import OpeninstallModule from 'openinstall-react-native'
```
调用如下方法，可重复获取（理论上可在任意位置获取安装参数）：
```
OpeninstallModule.getInstall(10, map => {
  if (map) {
    //do your work here
  }        
  Alert.alert('安装回调',JSON.stringify(map))     
})
```
- 第一个传入的参数为超时时长，一般为10s左右，如果只是在后台默默统计或使用，可以设置更长时间；第二个函数返回的是map或字典，包含动态安装参数（data）和渠道参数（channel），注意：只有通过渠道二维码或链接安装app后，才会有渠道参数
- 如果动态安装参数（data）和渠道参数（channel）同时为空，则map返回null
- 对iOS，该方法尽量写在业务场景需要参数的位置调用（在业务场景时，网络一般都是畅通的），例如，可以选择在用户注册成功后调用该方法获取参数，对用户进行奖励。原因是iOS首次安装、首次启动的app，会询问用户获取网络权限，用户允许后SDK才能正常联网去获取参数。如果调用过早，可能导致网络权限还未允许就被调用，导致参数无法及时拿到，误以为参数不存在

### 4 渠道统计 <span style="margin-left: 5px;display: inline-block;background: red;color: #fff;border-radius: 3px;padding: 2px 3px;font-size: 12px;">高级版功能</span>
SDK 会自动完成访问量、点击量、安装量、活跃量、留存率等统计工作。

#### （1）上报注册事件
在用户注册成功时，可调用该方法上报注册事件，需要导入'openinstall-react-native'
```
OpeninstallModule.reportRegister()
```
#### （2）上报效果点
两个参数分别为 效果点的ID，string类型，以及 效果点的值，为整型，示例：
```
OpeninstallModule.reportEffectPoint('effect_test',1)
```

## 二、手动集成方式（如果自动集成方式ok的，则无需进行手动集成）
以下分别为iOS和android的手动集成方式
- 如果在执行自动集成脚本时发生错误，请使用以下手动集成方式。

### iOS 手动集成方式

在 `react-native link` 之后，打开 iOS 工程。

#### 1 相关配置

##### （1）初始化配置
在 `Info.plist` 文件中配置 appKey 键值对，如下：
``` plist
<key>com.openinstall.APP_KEY</key>
<string>从openinstall官网后台获取应用的appkey</string>
```
##### （2）universal links配置（iOS9以后推荐使用）

对于iOS，为确保能正常跳转，AppID必须开启Associated Domains功能，请到[苹果开发者网站](https://developer.apple.com)，选择Certificate, Identifiers & Profiles，选择相应的AppID，开启Associated Domains。注意：当AppID重新编辑过之后，需要更新相应的mobileprovision证书。(详细配置步骤请看[openinstall官网](https://www.openinstall.io)后台文档，universal link从后台获取)，如果已经开启过Associated Domains功能，进行下面操作：

- 在左侧导航器中点击您的项目
- 选择 `Capabilities` 标签
- 打开 `Associated Domains` 开关
- 添加 openinstall 官网后台中应用对应的关联域名（iOS集成->iOS应用配置->关联域名(Associated Domains)）

##### （3）scheme配置

在 `Info.plist` 文件中，在 `CFBundleURLTypes` 数组中添加应用对应的 `scheme`

``` plist
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

### android 手动集成方式
在 `react-native link` 之后，打开 android 工程。

#### android 检查相关配置

##### 1 Checkout settings.gradle
检查 android 项目下的 settings.gradle 配置有没有包含以下内容：
```
include ':app', ':openinstall-react-native', ':openinstall-react-native'
project(':openinstall-react-native').projectDir = new File(rootProject.projectDir, '../node_modules/openinstall-react-native/android')

```

##### 2 检查build.gradle配置
检查一下 dependencies 中有没有添加 openinstall-react-native 依赖

````
dependencies {
    ...
    implementation project(':openinstall-react-native')  
}
````

##### 3 检查 app 下的 AndroidManifest 配置
your react native project/android/app/AndroidManifest.xml


在AndroidManifest.xml的application标签内设置AppKey
```
 <meta-data android:name="com.openinstall.APP_KEY" android:value="OPENINSTALL_APPKEY"/>  

```


在AndroidManifest.xml的拉起页面activity标签中添加intent-filter（一般为MainActivity），配置scheme，用于浏览器中拉起

```
<activity
    android:name=".MainActivity"
    android:launchMode="singleTask">
    <intent-filter>
        <action android:name="android.intent.action.VIEW"/>
        <category android:name="android.intent.category.DEFAULT"/>
        <category android:name="android.intent.category.BROWSABLE"/>
        <data android:scheme="OPENINSTALL_SCHEME"/>
    </intent-filter>
</activity>

```
##### 注意:OPENINSTALL_APPKEY为openinstall官方分配的appKey，OPENINSTALL_SCHEME为openinstall官方分配的scheme

现在重新 sync 一下项目，应该能看到 openinstall-react-native作为 android Library 项目导进来了。


