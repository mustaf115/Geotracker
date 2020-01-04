/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, {useEffect} from 'react';
import {
  SafeAreaView,
  StyleSheet,
  Text,
  StatusBar,
  NativeModules,
  NativeEventEmitter,
  PermissionsAndroid,
  TouchableOpacity,
  Button
} from 'react-native';

const Location = NativeModules.Location;

const App = () => {

  useEffect(() => {
    const ask = async () => {
      const granted = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
        {
          message: 'Need location, accept',
          buttonPositive: 'OK',
          buttonNegative: 'no'
        }
      );
    };
    ask();
    
    const eventEmitter = new NativeEventEmitter(Location);
    eventEmitter.addListener('WatchLocation', event => {
      console.log(event);
      fetch('http://192.168.1.12:8080/', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(event)
      })
      // fetch('http://localhost:8080/')
        .catch( err => console.error(err));
    });

    return () => {
      eventEmitter.removeAllListeners();
    };
  });

  Location.getLocation();
  
  return (
    <>
      <StatusBar barStyle="dark-content" />
      <SafeAreaView style={styles.view}>
        <Text>Hello, world!</Text>
        <Button
          title="Ask for location"
          onPress={() => Location.watchLocation()}
        />
        <Button
          title="Stop"
          onPress={() => Location.stopLocation()}
        />
      </SafeAreaView>
    </>
  );
};

const styles = StyleSheet.create({
  view: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center'
  }
});

export default App;
