## android 手动集成方式
在 `react-native link` 之后，打开 android 工程。

##### 1 检查build.gradle配置
检查一下 dependencies 中有没有添加 openinstall-react-native 依赖。  
your react native project/android/app/build.gradle


````
android {
    defaultConfig {
        applicationId "yourApplicationId"
        ...
        manifestPlaceholders = [
                OPENINSTALL_APPKEY: "yourAppKey", //在此替换你的APPKey
        ]
    }
}
dependencies {
    implementation fileTree(dir: "libs", include: ["*.jar"])
    implementation project(':openinstall-react-native')  // 添加 openinstall 依赖
    implementation "com.facebook.react:react-native:+"  // From node_modules
}
````

##### 2 检查settings.gradle配置
检查 android 项目下的 settings.gradle 配置有没有包含以下内容：  
settings.gradle  

include ':app', ':openinstall-react-native', ':openinstall-react-native'
project(':openinstall-react-native').projectDir = new File(rootProject.projectDir, '../node_modules/openinstall-react-native/android')

##### 3 检查 app 下的 AndroidManifest 配置
your react native project/android/app/AndroidManifest.xml


在AndroidManifest.xml的application标签内设置AppKey  
```
 <meta-data android:name="com.openinstall.APP_KEY" android:value="${OPENINSTALL_APPKEY}"/>

```

在AndroidManifest.xml的拉起页面activity标签中添加intent-filter（一般为MainActivity），配置scheme，用于浏览器中拉起  
(scheme详细获取位置：openinstall应用控制台->Android集成->Android应用配置)  

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

现在重新 sync 一下项目，应该能看到 openinstall-react-native作为 android Library 项目导进来了。  


package需要在MainApplication.java文件中的getPackages方法中提供。这个文件位于我们的reactNative应用文件夹的android目录中
```
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
              new MainReactPackage(),
              new OpeninstallReactPackage()); // 注册OpeninstallReactPackage包管理器
    }

```
