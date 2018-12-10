/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */
 
import React, {Component} from 'react';
import { Alert, AppRegistry, Button, StyleSheet, View} from 'react-native';
import OpeninstallModule from 'openinstall-react-native'


export default class App extends Component {
  _onInstallButton() {
	OpeninstallModule.getInstall(3, map => {
		if (map) {
			//do your work here
		}        
		Alert.alert('安装回调',JSON.stringify(map))     
		})
  }

  _onRegisterButton() {
    OpeninstallModule.reportRegister()
    Alert.alert("reportRegister");
  }

  _onEffectButton() {
   OpeninstallModule.reportEffectPoint('effect_test',1)
    Alert.alert("reportEffectPoint");
  }
 
 componentDidMount() {
  //该方法用于监听app通过univeral link或scheme拉起后获取唤醒参数
  this.receiveWakeupListener = map => {
     if (map) {
        Alert.alert('拉起回调',JSON.stringify(map))

	   //do your work here
     }        
  } 
  OpeninstallModule.addWakeUpListener(this.receiveWakeupListener)  
  };

  componentWillUnMount() {
  OpeninstallModule.removeWakeUpListener(this.receiveWakeupListener)//移除监听
}


  render() {
    return (
      <View style={styles.container}>
        <View style={styles.buttonContainer}>
          <Button
            onPress={this._onInstallButton}
            title="获取安装参数"
          />
        </View>
        <View style={styles.buttonContainer}>
          <Button
            onPress={this._onRegisterButton}
            title="注册上报"
            color="#841584"
          />
        </View>
        <View style={styles.buttonContainer}>
          <Button
            onPress={this._onEffectButton}
            title="效果点上报"
          />
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
   flex: 1,
   justifyContent: 'center',
  },
  buttonContainer: {
    margin: 20
  },
  alternativeLayoutButtonContainer: {
    margin: 20,
    flexDirection: 'row',
    justifyContent: 'space-between'
  }
})
