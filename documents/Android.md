## android 手动集成方式

**React Native 0.60 开始，会自动 link 原生模块 ，所以只需要 [配置 openinstall](#step4) 即可**


在 `react-native link` 之后，打开 android 工程。

#### 配置项目
检查 android 项目下的 settings.gradle 配置有没有包含以下内容：  
project/android/settings.gradle  
``` gradle
project(':openinstall-react-native').projectDir = new File(rootProject.projectDir, '../node_modules/openinstall-react-native/android')
include ':app', ':openinstall-react-native'
```
#### 导入项目
检查一下 dependencies 中有没有添加 openinstall-react-native 依赖。  
project/android/app/build.gradle

``` gradle
dependencies {
    implementation fileTree(dir: "libs", include: ["*.jar"])
    implementation project(':openinstall-react-native')  // 添加 openinstall 依赖
    implementation "com.facebook.react:react-native:+" 
}
```

现在重新 sync 一下项目，能看到 `openinstall-react-native` 作为 android Library 项目导进来了。  
#### 注册模块
`OpeninstallReactPackage` 需要在 `MainApplication.java` 文件中的 `getPackages` 方法中提供。  

``` java
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
              new MainReactPackage(),
              new OpeninstallReactPackage());
    }

```

#### 配置 openinstall
<a id='step4'></a>
project/android/app/AndroidManifest.xml

在AndroidManifest.xml的application标签内设置AppKey  
``` xml
 <meta-data android:name="com.openinstall.APP_KEY"
            android:value="OPENINSTALL_APPKEY"/>

```

在AndroidManifest.xml的拉起页面activity标签中添加intent-filter（一般为MainActivity），配置scheme，用于浏览器中拉起  
(scheme详细获取位置：openinstall应用控制台 -> Android集成 -> Android应用配置)  

``` xml
<activity
    android:name=".MainActivity"
    android:launchMode="singleTask">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
    <intent-filter>
        <action android:name="android.intent.action.VIEW"/>
        <category android:name="android.intent.category.DEFAULT"/>
        <category android:name="android.intent.category.BROWSABLE"/>
        <data android:scheme="OPENINSTALL_SCHEME"/>
    </intent-filter>
</activity>

```


