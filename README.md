# Openinstall React Native Plugin
openinstall-react-native 是openinstall官方开发的 React Native 插件，使用该插件可以方便快速地集成openinstall相关功能。

**针对使用了 渠道统计 功能中的 广告渠道 效果监测功能的集成，需要参考 [补充文档](#ad)**

**从1.3.0开始，插件将不再自动初始化，需要用户手动调用初始化接口**

# 一、使用 npm 安装插件

在您的项目根目录下执行

```
npm install openinstall-react-native --save
```

**React Native 0.60 之前**  

```
react-native link
```
- link的时候如果出现 `Error: Cannot find module 'asap/raw'` 则先执行 `npm install` 再 `react-native link` 就好了

**React Native 0.60 之后**  

如果你的iOS项目是通过Cocoapods来集成React Native（即原生应用集成react-native），可通过如下步骤安装本插件。（注意：使用 pod 就无须执行 react-native link 了，否则会有冲突。）

1. 在ios/Podfile文件中添加如下代码：
```
 pod 'openinstall-react-native', :path => '../node_modules/openinstall-react-native'
```
2. 在Podfile文件所在目录下执行命令：
```
 pod install
```

## 二、自动集成方式
### 如自动集成方式失败，无需惊慌，请参考手动集成方式 [手动集成文档](https://github.com/OpenInstall/openinstall-react-native/tree/master/documents)

（1）使用自动集成脚本集成代码和部分配置
```
npm run openinstall <yourAppKey> <yourScheme>
```
- yourAppKey指的是你在openinstall官方账号后台，创建应用后分配的AppKey
- yourScheme指的是你在openinstall官方账号后台，创建应用后分配的scheme  
(scheme详细获取位置：openinstall应用控制台->Android集成->Android应用配置，iOS同理）  

举例：
```
npm run openinstall e7iomw rc8tey
```
（2）xcode配置（只对iOS）  

在 iOS 工程中，如果要使用universal links(通用链接)的拉起功能，需要开启 Associated Domains功能，请到[苹果开发者网站](https://developer.apple.com)，选择Certificate, Identifiers & Profiles，选择相应的AppID，开启Associated Domains。注意：当AppID重新编辑过之后，需要更新相应的mobileprovision证书。(详细配置步骤请看[React-Native接入指南](https://www.openinstall.io/doc/RN_sdk.html))，如果已经开启过Associated Domains功能，进行下面操作：  
- 在左侧导航器中点击您的项目
- 选择 `Capabilities` 标签
- 打开 `Associated Domains` 开关
- 添加 openinstall 官网后台中应用对应的关联域名（openinstall应用控制台->iOS集成->iOS应用配置->关联域名(Associated Domains)）

#### 注意：

- 在 iOS 工程中如果找不到头文件可能要在 TARGETS-> BUILD SETTINGS -> Search Paths -> Header Search Paths 添加如下如路径：
````
$(SRCROOT)/../node_modules/openinstall-react-native/ios/RCTOpenInstall
````

## 三、使用指南
### 1. 初始化

请在 react native 端入口 js 文件中（例如 `App.js` 文件）导入：
``` js
import OpeninstallModule from 'openinstall-react-native'
```
确保用户同意《隐私政策》之后，再初始化 openinstall。参考 [应用合规指南](https://www.openinstall.io/doc/rules.html)
``` js
OpeninstallModule.init()
```
**iOS用户使用插件1.3.0之前的版本升级到新版本，需要参考新的手动集成文档修改**

### 2. 快速下载
如果只需要快速下载功能，无需其它功能（携带参数安装、渠道统计、一键拉起），完成sdk初始化即可（自动集成方式或手动集成方式）

### 3. 一键跳转

通常在 `componentDidMount` 中注册唤醒回调监听
``` js
componentDidMount() {
  //该方法用于监听app通过univeral link或scheme拉起后获取唤醒参数
  this.receiveWakeupListener = map => {
     if (map) {
	   //do your work here
     }        
   Alert.alert('唤醒参数', JSON.stringify(map)) 
  } 
  OpeninstallModule.addWakeUpListener(this.receiveWakeupListener)  
}

componentWillUnMount() {
  OpeninstallModule.removeWakeUpListener(this.receiveWakeupListener)//移除监听
}
```
- 第二个函数返回的是map或字典，包含动态安装参数（data）和渠道参数（channel），注意：只有通过渠道二维码或链接安装app后，才会有渠道参数
- 如果动态安装参数（data）和渠道参数（channel）同时为空，则map返回null

### 4. 携带参数安装（高级版功能）
在需要获取安装参数的位置，导入插件：
```
import OpeninstallModule from 'openinstall-react-native'
```
调用如下方法，可重复获取（理论上可在任意位置获取安装参数）：
``` js
OpeninstallModule.getInstall(10, map => {
  if (map) {
    //do your work here
  }        
  Alert.alert('安装参数', JSON.stringify(map))     
})
```
- 第一个传入的参数为超时时长，一般为10s左右，如果只是在后台默默统计或使用，可以设置更长时间；第二个函数返回的是map或字典，包含动态安装参数（data）和渠道参数（channel），注意：只有通过渠道二维码或链接安装app后，才会有渠道参数
- 如果动态安装参数（data）和渠道参数（channel）同时为空，则map返回null
- 对iOS，该方法尽量写在业务场景需要参数的位置调用（在业务场景时，网络一般都是畅通的），例如，可以选择在用户注册成功后调用该方法获取参数，对用户进行奖励。原因是iOS首次安装、首次启动的app，会询问用户获取网络权限，用户允许后SDK才能正常联网去获取参数。如果调用过早，可能导致网络权限还未允许就被调用，导致参数无法及时拿到，误以为参数不存在

### 5. 渠道统计（高级版功能）
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

## 四、导出apk/api包并上传
- 代码集成完毕后，需要导出安装包上传openinstall后台，openinstall会自动完成所有的应用配置工作。  
- 上传完成后即可开始在线模拟测试，体验完整的App安装/拉起流程；待测试无误后，再完善下载配置信息。  

<a id="ad"></a>
## 广告平台接入补充文档

**Android 平台**  
（1）针对广告平台接入，新增配置接口，在调用 init 之前调用。参考 [广告平台对接Android集成指引](https://www.openinstall.io/doc/ad_android.html)
``` js
    /**
    * adEnabled 为 true 表示 openinstall 需要获取广告追踪相关参数，默认为 false
    * oaid 为 null 时，表示交由 openinstall 获取 oaid， 默认为 null
    * gaid 为 null 时，表示交由 openinstall 获取 gaid， 默认为 null
    */
    OpeninstallModule.config(true, "通过移动安全联盟获取到的 oaid", "通过 google api 获取到的 advertisingId");
```
例如： 开发者自己获取到了 oaid，但是需要 openinstall 获取 gaid，则调用代码为
``` js
    // f32a09dc-3312-d43e-6583-62fac13f33ae 是通过移动安全联盟获取到的 oaid
    OpeninstallModule.config(true, "f32a09dc-3312-d43e-6583-62fac13f33ae", null);
```
（2）为了精准地匹配到渠道，需要获取设备唯一标识码（IMEI），因此需要做额外的权限申请    
在 `AndroidMainfest.xml` 配置文件中添加需要申请的权限 `<uses-permission android:name="android.permission.READ_PHONE_STATE"/>`

（3）申请权限并初始化   
导入包
``` js
import { PermissionsAndroid } from 'react-native'
```
在 `App.js` 的 `componentDidMount` 方法中初始化，并且在初始化之前申请权限
``` js
this.receiveWakeupListener = map => {    
    Alert.alert('唤醒参数', JSON.stringify(map)) 
} 
var permission = PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE;
PermissionsAndroid.request(permission)
    .then(granted => {
        if (granted === PermissionsAndroid.RESULTS.GRANTED){
            //权限获取成功
        }else{
            //权限获取失败
        }
        OpeninstallModule.config(true, null, null);
        OpeninstallModule.init();
        OpeninstallModule.addWakeUpListener(this.receiveWakeupListener)
    })
```

**iOS 平台**  
使用第三方插件获取iOS idfa 并传给 openinstall 插件进行初始化
``` js
var options = {adid: "通过第三方插件获取的 iOS idfa"}
OpeninstallModule.init(options);
```