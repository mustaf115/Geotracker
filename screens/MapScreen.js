import React, {useEffect, useState} from 'react';
import {
  StyleSheet,
  Text,
  StatusBar,
  NativeModules,
  PermissionsAndroid,
  View,
  Button,
  Dimensions,
  NativeEventEmitter
} from 'react-native';
import MapView, { Marker } from 'react-native-maps';

const Location = NativeModules.Location;
const width = Dimensions.get('window').width;
const height = Dimensions.get('window').height;


const MapScreen = ({navigation: {push}}) => {

  const [latitude, setLatitude] = useState(0);
  const [longitude, setLongitude] = useState(0);
  const [isTracking, setIsTracking] = useState(false);
  
  useEffect(() => {
    const eventEmitter = new NativeEventEmitter(Location);
    eventEmitter.addListener('NewLocation', event => {
      const { latitude, longitude } = event;
      setLatitude(latitude);
      setLongitude(longitude);
    });

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
      eventEmitter.removeAllListeners('NewLocation');
      return;
    };
  });

  const track = () => {
    Location.track();
    setIsTracking(true);
  };

  const untrack = () => {
    Location.untrack();
    setIsTracking(false);
  };

  const logout = () => {
    Location.removeToken();
    untrack();
    push('Login');
  };
  
  return (
    <>
      <StatusBar barStyle="dark-content" />
      <View style={styles.view}>
        <MapView
          style={{width, height: height / 2}}
          region={{
            latitude,
            longitude,
            latitudeDelta: 0.01,
            longitudeDelta: 0
          }}
        >
          <Marker
            coordinate={{
              latitude,
              longitude
            }}
          />
        </MapView>
        <Text>Hello, world!</Text>
        <View style={styles.button}>
          <Button
            title="Ask for location"
            onPress={() => track()}
          />
        </View>
        <View style={styles.button}>
          <Button
            title="Stop"
            onPress={() => untrack()}
          />
        </View>
        <View style={styles.button}>
          <Button
            title="Logout"
            onPress={() => logout()}
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
