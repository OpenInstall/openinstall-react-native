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
import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppInstallAdapter;
import com.fm.openinstall.listener.AppWakeUpAdapter;
import com.fm.openinstall.model.AppData;

public class OpeninstallModule extends ReactContextBaseJavaModule {
    ReactContext context;

    public OpeninstallModule(final ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;
        reactContext.addActivityEventListener(new ActivityEventListener() {
            @Override
            public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

            }

            @Override
            public void onNewIntent(Intent intent) {
                Log.d("OpenInstallModule", "onNewIntent");
                getWakeUp(intent, null);
            }
        });
    }

    @ReactMethod
    public void getWakeUp(final Callback successBack) {
        Log.d("OpenInstallModule", "getWakeUp");
        Activity currentActivity = getCurrentActivity();
        if (currentActivity != null) {
            Intent intent = currentActivity.getIntent();
            getWakeUp(intent, successBack);
        }

    }

    private void getWakeUp(Intent intent, final Callback callback) {
        OpenInstall.getWakeUp(intent, new AppWakeUpAdapter() {
            @Override
            public void onWakeUp(AppData appData) {
                if (appData != null) {
                    Log.d("OpenInstallModule", "getWakeUp : wakeupData = " + appData.toString());
                    String channel = appData.getChannel();
                    String data = appData.getData();
                    WritableMap params = Arguments.createMap();
                    params.putString("channel", channel);
                    params.putString("data", data);

                    if (callback == null) {
                        getReactApplicationContext()
                                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                .emit("OpeninstallWakeupCallBack", params);
                    } else {
                        callback.invoke(params);
                    }

                }
            }
        });
    }

    @Override
    public void initialize() {
        super.initialize();
        OpenInstall.init(context);
    }

    @Override
    public String getName() {
        return "OpeninstallModule";
    }

    @ReactMethod
    public void getInstall(Integer time, final Callback callback) {
        Log.d("OpenInstallModule", "getInstall");
        OpenInstall.getInstall(new AppInstallAdapter() {
            @Override
            public void onInstall(AppData appData) {
                try {
                    Log.d("OpenInstallModule", "getInstall : data = " + appData.toString());
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
        Log.d("OpenInstallModule", "reportRegister");
        OpenInstall.reportRegister();
    }

    @ReactMethod
    public void reportEffectPoint(String pointId, Integer pointValue) {
        Log.d("OpenInstallModule", "reportEffectPoint");
        if (!TextUtils.isEmpty(pointId) && pointValue >= 0) {
            OpenInstall.reportEffectPoint(pointId, pointValue);
        }
    }
}
