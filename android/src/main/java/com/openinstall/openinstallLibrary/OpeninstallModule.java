package com.openinstall.openinstallLibrary;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppInstallAdapter;
import com.fm.openinstall.listener.AppWakeUpAdapter;
import com.fm.openinstall.model.AppData;

public class OpeninstallModule extends ReactContextBaseJavaModule{
    ReactContext  context;

    public OpeninstallModule(final ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;
    }

    @ReactMethod
    public void getWakeUp(final Callback successBack){
         Activity currentActivity = getCurrentActivity();
         Intent intent= currentActivity.getIntent();
          OpenInstall.getWakeUp(intent, new AppWakeUpAdapter() {
              @Override
              public void onWakeUp(AppData appData) {
                  if (appData!=null) {
                      Log.d("OpenInstallModule", "getWakeUp : wakeupData = " + appData.toString());
                      String channel = appData.getChannel();
                      String data = appData.getData();
                      WritableMap params = Arguments.createMap();
                      params.putString("channel", channel);
                      params.putString("data", data);
                      successBack.invoke(params);
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
    public void getInstall(Integer time,final Callback  callback ){

        OpenInstall.getInstall(new AppInstallAdapter() {
            @Override
            public void onInstall(AppData appData) {
                try {
                        Log.d("OpenInstall", "getInstall : data = " + appData.toString());
                        String channelCode = appData.getChannel();
                        String data = appData.getData();
                        WritableMap params = Arguments.createMap();
                        params.putString("channel",channelCode);
                        params.putString("data",data);
                        callback.invoke(params);
                }catch (Exception e){
                    callback.invoke(e);
                }
            }
        },time*1000);
    }

    @ReactMethod
    public void reportRegister(){
        OpenInstall.reportRegister();
    }

    @ReactMethod
    public void reportEffectPoint(String pointId,Integer pointValue){
      if (!TextUtils.isEmpty(pointId) && pointValue>=0){
          OpenInstall.reportEffectPoint(pointId,pointValue);
      }
    }
}
