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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class OpeninstallModule extends ReactContextBaseJavaModule {

    private static final String TAG = "OpenInstallModule";

    public static final String EVENT = "OpeninstallWakeupCallBack";
    private ReactContext context;
    private Intent wakeupIntent = null;
    private volatile boolean initialized = false;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

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

    @ReactMethod
    public void init() {
        Log.d(TAG, "init");
        if (!initialized) {
            OpenInstall.init(context);
            initialized = true;
            countDownLatch.countDown();
            if (wakeupIntent != null) {
                OpenInstall.getWakeUp(wakeupIntent, new AppWakeUpAdapter() {
                    @Override
                    public void onWakeUp(AppData appData) {
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
                            wakeupIntent = null;
                        }
                    }
                });

            }
        }
    }

    @ReactMethod
    public void getWakeUp(final Callback successBack) {
        Log.d(TAG, "getWakeUp");
        Activity currentActivity = getCurrentActivity();
        if (currentActivity != null) {
            Intent intent = currentActivity.getIntent();
            getWakeUp(intent, successBack);
        }

    }

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

                        if (callback == null) {
                            getReactApplicationContext()
                                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                    .emit(EVENT, params);
                        } else {
                            callback.invoke(params);
                        }

                    }
                }
            });
        } else {
            wakeupIntent = intent;
        }
    }

    @Override
    public String getName() {
        return "OpeninstallModule";
    }

    @ReactMethod
    public void getInstall(Integer time, final Callback callback) {
        Log.d(TAG, "getInstall");
        waitInit();
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
        waitInit();
        OpenInstall.reportRegister();
    }

    @ReactMethod
    public void reportEffectPoint(String pointId, Integer pointValue) {
        Log.d(TAG, "reportEffectPoint");
        waitInit();
        if (!TextUtils.isEmpty(pointId) && pointValue >= 0) {
            OpenInstall.reportEffectPoint(pointId, pointValue);
        }
    }

    private void waitInit() {
        try {
            countDownLatch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
