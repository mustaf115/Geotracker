import React from 'react';
import { View, Text, TextInput, StyleSheet, Button } from 'react-native';

export default ({navigation: {navigate}}) => {

  return (
    <View style={styles.view}>
      <Text>Geotracker</Text>
      <TextInput style={styles.input} placeholder="Login" />
      <TextInput style={styles.input} placeholder="Password" />
      <Button 
        title="Login"
        onPress={() => navigate('Map')}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  view: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center'
  },
  input: {
    padding: 5,
    margin: 5,
    width: '50%',
    borderColor: '#000',
    borderWidth: 1
  }
});