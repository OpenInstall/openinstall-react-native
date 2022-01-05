import {
    NativeModules,
    Platform,
    DeviceEventEmitter,
} from 'react-native';

const OpeninstallModule = NativeModules.OpeninstallModule;

const receiveWakeUpEvent = 'OpeninstallWakeupCallBack'
const listeners = {}

export default class openinstall{

  // 旧版本接口（1.5.0版本废除）
  static config(adEnabled, oaid, gaid){
      if(Platform.OS === 'android'){
		  var options = {
			adEnabled : adEnabled,
			oaid : oaid,
			gaid : gaid,
		  }
		  OpeninstallModule.config(options)
	  }else{
		  // ignore, platform unsupport
	  }
  }
  
  // 1.5.0版本新增，替换config(adEnabled, oaid, gaid)接口
  static configAndroid(options){
      if(Platform.OS === 'android'){
		  OpeninstallModule.config(options)
	  }else{
		  // ignore, platform unsupport
	  }
  }

/**
 * 初始化
 */
  static init(options){
	if(Platform.OS == 'android'){
		OpeninstallModule.init()
	}else{
		OpeninstallModule.initSDK(options)
	}
  }

/**
 * 获取安装动态参数
 * @param {Int} s 超时时长,一般为10~15s 如果只是在后台默默统计或使用参数，可以设置更长时间
 * @param {Function} cb = (result)=> {} data和channel都为空时返回null
 */
  static getInstall(s,cb){
    OpeninstallModule.getInstall(s,result => {
        cb(result)
      }
    )
  }
  
/**
 * 获取安装动态参数，不会中断初始化读取匹配数据，重试可获取数据
 * @param {Int} s 超时时长,一般为10~15s 如果只是在后台默默统计或使用参数，可以设置更长时间
 * @param {Function} cb = (result)=> {} data和channel都为空时返回null
 */
  static getInstallCanRetry(s,cb){
	if(Platform.OS == 'android'){
		OpeninstallModule.getInstallCanRetry(s,result => {
			cb(result)
		  }
		)
	}
  }
 
/**
 * 监听univeral link或scheme拉起参数回调的方法
 * @param {Function} cb = (result)=> {} data和channel都为空时返回null
 */
  static addWakeUpListener (cb) {
    OpeninstallModule.getWakeUp(
      result => {
        cb(result)
      }
    )
	listeners[cb] = DeviceEventEmitter.addListener(
		receiveWakeUpEvent,
		result => {
			cb(result)
		}
	)
    
  }
  
/**
 * Android 监听 scheme 拉起参数回调，不管是否有数据都会回调
 * @param {Function} cb = (result)=> {} data和channel都为空时返回null
 */
  static getWakeUpAlwaysCallback(cb){
	if(Platform.OS == 'android'){
		OpeninstallModule.getWakeUpAlwaysCallback(
		  result => {
			cb(result)
		  }
		)
		listeners[cb] = DeviceEventEmitter.addListener(
			receiveWakeUpEvent,
			result => {
				cb(result)
			}
		)
	}
  }

 /**
  * 取消监听
  * @param {Function} cb = (Object) => { }
  */
  static removeWakeUpListener (cb) {
    if (!listeners[cb]) {
      return
    }
    listeners[cb].remove()
    listeners[cb] = null
  }

/**
 * 上报注册事件
 */
  static reportRegister (){
    OpeninstallModule.reportRegister()
  }

/**
 * 上报效果点
 * @param {string} effectID
 * @param {number} effectValue
 */
  static reportEffectPoint (effectID, effectValue){
    OpeninstallModule.reportEffectPoint(effectID, effectValue)
  }

}

