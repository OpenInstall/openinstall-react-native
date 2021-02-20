package com.openinstall.openinstallLibrary;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.fm.openinstall.Configuration;
import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppInstallAdapter;
import com.fm.openinstall.listener.AppWakeUpAdapter;
import com.fm.openinstall.model.AppData;

public class OpeninstallModule extends ReactContextBaseJavaModule {

    private static final String TAG = "OpenInstallModule";

    public static final String EVENT = "OpeninstallWakeupCallBack";
    private ReactContext context;
    private Intent wakeupIntent = null;
    private WritableMap wakeupDataHolder = null;
    private boolean registerWakeup = false;
    private boolean callInit = false;
    private boolean initialized = false;
    private Configuration configuration = null;

    public OpeninstallModule(final ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;
        reactContext.addActivityEventListener(new ActivityEventListener() {
            @Override
            public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

            }

            @Override
            public void onNewIntent(Intent intent) {
                Log.d(TAG, "onNewIntent");
                getWakeUp(intent, null);
            }
        });
    }

    @Override
    public String getName() {
        return "OpeninstallModule";
    }

    @ReactMethod
    public void config(boolean adEnabled, String oaid, String gaid) {
        Configuration.Builder builder = new Configuration.Builder();
        builder.adEnabled(adEnabled);
        builder.oaid(oaid);
        builder.gaid(gaid);
        Log.d(TAG, String.format("config adEnabled=%b, oaid=%s, gaid=%s",
                adEnabled, oaid == null ? "NULL" : oaid, gaid == null ? "NULL" : gaid));
        configuration = builder.build();
    }

    @ReactMethod
    public void init() {
        callInit = true;
        OpenInstall.init(context, configuration);
        initialized();
    }

    private void initialized() {
        initialized = true;
        if (wakeupIntent != null) {
            OpenInstall.getWakeUp(wakeupIntent, new AppWakeUpAdapter() {
                @Override
                public void onWakeUp(AppData appData) {
                    wakeupIntent = null;
                    if (appData != null) {
                        Log.d(TAG, "getWakeUp : wakeupData = " + appData.toString());
                        String channel = appData.getChannel();
                        String data = appData.getData();
                        WritableMap params = Arguments.createMap();
                        params.putString("channel", channel);
                        params.putString("data", data);
                        getReactApplicationContext()
                                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                .emit(EVENT, params);
                    }
                }
            });
        }
    }

    @ReactMethod
    public void getWakeUp(final Callback successBack) {
        Log.d(TAG, "getWakeUp");
        registerWakeup = true;
        checkInit();
        if (wakeupDataHolder != null) {
            // 调用getWakeUp注册前就处理过拉起参数了(onNewIntent)
            getReactApplicationContext()
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(EVENT, wakeupDataHolder);
            wakeupDataHolder = null;
        } else {
            Activity currentActivity = getCurrentActivity();
            if (currentActivity != null) {
                Intent intent = currentActivity.getIntent();
                getWakeUp(intent, successBack);
            }
        }
    }

    // 可能在用户调用初始化之前调用
    private void getWakeUp(Intent intent, final Callback callback) {
        boolean isValid = OpenInstall.isValidIntent(intent);
        if (!isValid) { // 如果不是 openinstall 拉起，忽略
            return;
        }
        if (initialized) {
            OpenInstall.getWakeUp(intent, new AppWakeUpAdapter() {
                @Override
                public void onWakeUp(AppData appData) {
                    if (appData != null) {
                        Log.d(TAG, "getWakeUp : wakeupData = " + appData.toString());
                        String channel = appData.getChannel();
                        String data = appData.getData();
                        WritableMap params = Arguments.createMap();
                        params.putString("channel", channel);
                        params.putString("data", data);
                        if (registerWakeup) {
                            getReactApplicationContext()
                                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                    .emit(EVENT, params);
                        } else {
                            wakeupDataHolder = params;
                        }
                    }
                }
            });
        } else {
            wakeupIntent = intent;
        }
    }


    @ReactMethod
    public void getInstall(Integer time, final Callback callback) {
        Log.d(TAG, "getInstall");
        checkInit();
        OpenInstall.getInstall(new AppInstallAdapter() {
            @Override
            public void onInstall(AppData appData) {
                try {
                    Log.d(TAG, "getInstall : data = " + appData.toString());
                    String channelCode = appData.getChannel();
                    String data = appData.getData();
                    WritableMap params = Arguments.createMap();
                    params.putString("channel", channelCode);
                    params.putString("data", data);
                    callback.invoke(params);
                } catch (Exception e) {
                    callback.invoke(e);
                }
            }
        }, time);
    }

    @ReactMethod
    public void reportRegister() {
        Log.d(TAG, "reportRegister");
        checkInit();
        OpenInstall.reportRegister();
    }

    @ReactMethod
    public void reportEffectPoint(String pointId, Integer pointValue) {
        Log.d(TAG, "reportEffectPoint");
        checkInit();
        if (!TextUtils.isEmpty(pointId) && pointValue >= 0) {
            OpenInstall.reportEffectPoint(pointId, pointValue);
        }
    }

    private void checkInit() {
        if (!callInit) {
            Log.d(TAG, "插件从1.3.0开始，提供初始化接口，如未调用初始化调用其它接口，插件内部将使用默认配置初始化");
            init();
        }
    }
}
