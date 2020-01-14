/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, {useEffect} from 'react';
import {
  StyleSheet,
  Text,
  StatusBar,
  NativeModules,
  PermissionsAndroid,
  View,
  Button
} from 'react-native';

const Location = NativeModules.Location;

const MapScreen = ({navigation: {navigate}}) => {
    
  useEffect(() => {
    const ask = async () => {
      await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
        {
          message: 'Need location, accept',
          buttonPositive: 'OK',
          buttonNegative: 'no'
        }
      );
    };
    ask();

    return () => {
      return;
    };
  });
  
  return (
    <>
      <StatusBar barStyle="dark-content" />
      <View style={styles.view}>
        <Text>Hello, world!</Text>
        <View style={styles.button}>
          <Button
            title="Ask for location"
            onPress={() => Location.track()}
          />
        </View>
        <View style={styles.button}>
          <Button
            title="Stop"
            onPress={() => Location.untrack()}
          />
        </View>
        <View style={styles.button}>
          <Button
            title="Logout"
            onPress={() => navigate('Login')}
          />
        </View>
      </View>
    </>
  );
};

const styles = StyleSheet.create({
  view: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center'
  },
  button: {
    margin: 5
  }
});

export default MapScreen;
