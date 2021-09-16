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
import com.facebook.react.bridge.ReadableMap;
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
    private final ReactContext context;
    private Intent wakeupIntent = null;
    private WritableMap wakeupDataHolder = null;
    private boolean registerWakeup = false;
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

    private boolean optBoolean(ReadableMap map, String key) {
        if (map.hasKey(key)) {
            if (map.isNull(key)) return false;
            return map.getBoolean(key);
        }
        return false;
    }

    private String optString(ReadableMap map, String key) {
        if (map.hasKey(key)) {
            return map.getString(key);
        }
        return null;
    }

    @ReactMethod
    public void config(ReadableMap readableMap) {
        Configuration.Builder builder = new Configuration.Builder();
        builder.adEnabled(optBoolean(readableMap, "adEnabled"));
        builder.oaid(optString(readableMap, "oaid"));
        builder.gaid(optString(readableMap, "gaid"));
        if (optBoolean(readableMap, "macDisabled")) {
            builder.macDisabled();
        }
        if (optBoolean(readableMap, "imeiDisabled")) {
            builder.imeiDisabled();
        }
        configuration = builder.build();
        Log.d(TAG, String.format("Configuration: adEnabled = %s, oaid = %s, gaid = %s, " +
                        "macDisabled = %s, imeiDisabled = %s",
                configuration.isAdEnabled(), configuration.getOaid(), configuration.getGaid(),
                configuration.isMacDisabled(), configuration.isImeiDisabled()));
    }

    @ReactMethod
    public void init() {
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
        OpenInstall.getInstall(new AppInstallAdapter() {
            @Override
            public void onInstall(AppData appData) {
                Log.d(TAG, "getInstall : data = " + appData.toString());
                String channelCode = appData.getChannel();
                String data = appData.getData();
                WritableMap params = Arguments.createMap();
                params.putString("channel", channelCode);
                params.putString("data", data);
                callback.invoke(params);
            }
        }, time);
    }

    @ReactMethod
    public void reportRegister() {
        Log.d(TAG, "reportRegister");
        OpenInstall.reportRegister();
    }

    @ReactMethod
    public void reportEffectPoint(String pointId, Integer pointValue) {
        Log.d(TAG, "reportEffectPoint");
        if (!TextUtils.isEmpty(pointId) && pointValue >= 0) {
            OpenInstall.reportEffectPoint(pointId, pointValue);
        }
    }

}
