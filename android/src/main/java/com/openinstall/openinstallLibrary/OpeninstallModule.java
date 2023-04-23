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
import com.fm.openinstall.listener.AppInstallListener;
import com.fm.openinstall.listener.AppInstallRetryAdapter;
import com.fm.openinstall.listener.AppWakeUpAdapter;
import com.fm.openinstall.listener.AppWakeUpListener;
import com.fm.openinstall.listener.ResultCallback;
import com.fm.openinstall.model.AppData;
import com.fm.openinstall.model.Error;

import java.util.HashMap;
import java.util.Map;

public class OpeninstallModule extends ReactContextBaseJavaModule {

    private static final String TAG = "OpenInstallModule";

    public static final String EVENT = "OpeninstallWakeupCallBack";
    private final ReactContext context;
    private Intent wakeupIntent = null;
    private WritableMap wakeupDataHolder = null;
    private boolean registerWakeup = false;
    private boolean initialized = false;
    private Configuration configuration = null;

    private boolean alwaysCallback = false;

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

    private boolean hasTrue(ReadableMap map, String key) {
        if (map.hasKey(key)) {
            if (map.isNull(key)) return false;
            return map.getBoolean(key);
        }
        return false;
    }

    @ReactMethod
    public void config(ReadableMap readableMap) {
        Configuration.Builder builder = new Configuration.Builder();

        if (hasTrue(readableMap, "adEnabled")) {
            builder.adEnabled(true);
        }
        if (readableMap.hasKey("oaid")) {
            builder.oaid(readableMap.getString("oaid"));
        }
        if (readableMap.hasKey("gaid")) {
            builder.gaid(readableMap.getString("gaid"));
        }
        if (hasTrue(readableMap, "imeiDisabled")) {
            builder.imeiDisabled();
        }
        if (readableMap.hasKey("imei")) {
            builder.imei(readableMap.getString("imei"));
        }
        if (hasTrue(readableMap, "macDisabled")) {
            builder.macDisabled();
        }
        if (readableMap.hasKey("macAddress")) {
            builder.macAddress(readableMap.getString("macAddress"));
        }
        if (readableMap.hasKey("androidId")) {
            builder.androidId(readableMap.getString("androidId"));
        }
        if (readableMap.hasKey("serialNumber")) {
            builder.serialNumber(readableMap.getString("serialNumber"));
        }
        if (hasTrue(readableMap, "simulatorDisabled")) {
            builder.simulatorDisabled();
        }
        if (hasTrue(readableMap, "storageDisabled")) {
            builder.storageDisabled();
        }

        configuration = builder.build();

//        Log.d(TAG, String.format("Configuration: adEnabled = %s, oaid = %s, gaid = %s, " +
//                        "macDisabled = %s, imeiDisabled = %s",
//                configuration.isAdEnabled(), configuration.getOaid(), configuration.getGaid(),
//                configuration.isMacDisabled(), configuration.isImeiDisabled()));
    }

    @ReactMethod
    public void serialEnabled(boolean enabled) {
        OpenInstall.serialEnabled(enabled);
    }

    @ReactMethod
    public void clipBoardEnabled(boolean enabled) {
        OpenInstall.clipBoardEnabled(enabled);
    }

    @ReactMethod
    public void init() {
        if (context.hasCurrentActivity()) {
            OpenInstall.init(context.getCurrentActivity(), configuration);
        } else {
            Log.w(TAG, "init with context, not activity");
            OpenInstall.init(context, configuration);
        }
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
                        WritableMap params = putData2Map(appData);
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

    @ReactMethod
    public void getWakeUpAlwaysCallback(final Callback successBack) {
        Log.d(TAG, "getWakeUpAlwaysCallback");
        alwaysCallback = true;
        registerWakeup = true;
        if (wakeupDataHolder != null) {
            // 调用getWakeUpAlwaysCallback注册前就处理过拉起参数了(onNewIntent)
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
            if (alwaysCallback) {
                OpenInstall.getWakeUpAlwaysCallback(intent, new AppWakeUpListener() {
                    @Override
                    public void onWakeUpFinish(AppData appData, Error error) {
                        if (error != null) {
                            Log.d(TAG, "getWakeUpAlwaysCallback : " + error.toString());
                        }
                        WritableMap params = putData2Map(appData);
                        if (registerWakeup) {
                            getReactApplicationContext()
                                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                    .emit(EVENT, params);
                        } else {
                            wakeupDataHolder = params;
                        }
                    }
                });
            } else {
                OpenInstall.getWakeUp(intent, new AppWakeUpAdapter() {
                    @Override
                    public void onWakeUp(AppData appData) {
                        WritableMap params = putData2Map(appData);
                        if (registerWakeup) {
                            getReactApplicationContext()
                                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                    .emit(EVENT, params);
                        } else {
                            wakeupDataHolder = params;
                        }
                    }
                });
            }
        } else {
            wakeupIntent = intent;
        }
    }

    @ReactMethod
    public void getInstall(Integer time, final Callback callback) {
        OpenInstall.getInstall(new AppInstallListener() {
            @Override
            public void onInstallFinish(AppData appData, Error error) {
                WritableMap params = putData2Map(appData);
                putError2Map(params, error);
                callback.invoke(params);
            }
        }, time);
    }

    @ReactMethod
    public void getInstallCanRetry(Integer time, final Callback callback) {
        OpenInstall.getInstallCanRetry(new AppInstallRetryAdapter() {
            @Override
            public void onInstall(AppData appData, boolean retry) {
                WritableMap params = putData2Map(appData);
                params.putBoolean("retry", retry);
                params.putBoolean("shouldRetry", retry);
                callback.invoke(params);
            }
        }, time);
    }

    @ReactMethod
    public void reportRegister() {
        OpenInstall.reportRegister();
    }

//    @ReactMethod
//    public void reportEffectPoint(String pointId, Integer pointValue) {
//        if (!TextUtils.isEmpty(pointId) && pointValue >= 0) {
//            OpenInstall.reportEffectPoint(pointId, pointValue);
//        }else {
//            Log.w(TAG, "reportEffectPoint 调用失败：pointId 不能为空，pointValue 必须大于0");
//        }
//    }

    @ReactMethod
    public void reportEffectPoint(String pointId, Integer pointValue, ReadableMap readableMap) {
        if (!TextUtils.isEmpty(pointId) && pointValue >= 0) {
            HashMap<String, String> extraMap = null;
            if (readableMap != null) {
                extraMap = new HashMap<>();
                HashMap<String, Object> map = readableMap.toHashMap();
                for (Map.Entry<String, ?> entry : map.entrySet()) {
                    String name = entry.getKey();
                    Object value = entry.getValue();
                    if (value == null) continue;
                    if (value instanceof String) {
                        extraMap.put(name, (String) value);
                    } else {
                        extraMap.put(name, value.toString());
                    }
                }
            }
            OpenInstall.reportEffectPoint(pointId, pointValue, extraMap);
        } else {
            Log.w(TAG, "reportEffectPoint 调用失败：pointId 不能为空，pointValue 必须大于0");
        }
    }

    @ReactMethod
    public void reportShare(String shareCode, String sharePlatform, final Callback callback) {
        if (TextUtils.isEmpty(shareCode) || TextUtils.isEmpty(sharePlatform)) {
            Log.w(TAG, "reportShare 调用失败：shareCode 和 sharePlatform 不能为空");
            WritableMap params = Arguments.createMap();
            params.putBoolean("shouldRetry", false);
            params.putString("message", "shareCode 和 sharePlatform 不能为空");
            callback.invoke(params);
        } else {
            OpenInstall.reportShare(shareCode, sharePlatform, new ResultCallback<Void>() {
                @Override
                public void onResult(Void v, Error error) {
                    WritableMap params = Arguments.createMap();
                    putError2Map(params, error);
                    callback.invoke(params);
                }
            });
        }
    }

    private WritableMap putData2Map(AppData appData) {
        WritableMap params = Arguments.createMap();
        if (appData != null) {
            params.putString("channel", appData.getChannel());
            params.putString("data", appData.getData());
        }
        return params;
    }

    private WritableMap putError2Map(WritableMap params, Error error) {
        if (params == null) {
            params = Arguments.createMap();
        }
        params.putBoolean("shouldRetry", error != null && error.shouldRetry());
        if (error != null) {
            params.putString("message", error.getErrorMsg());
        }
        return params;
    }

}
