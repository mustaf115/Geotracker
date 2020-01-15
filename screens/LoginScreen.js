import React, { useState } from 'react';
import { View, Text, TextInput, StyleSheet, Button } from 'react-native';

export default ({navigation: {navigate}}) => {
  const [ login, setLogin ] = useState('');
  const [ password, setPassword ] = useState('');

  const sendLogin = async (login, password) => {
    const msg = await fetch('http://192.168.1.12:8080/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({login, password})
    });
    if(!msg.ok) return;
    navigate('Map');
  };
  return (
    <View style={styles.view}>
      <Text>Geotracker</Text>
      <TextInput
        style={styles.input}
        value={login}
        onChangeText={ t => setLogin(t) }
        placeholder="Login"
      />
      <TextInput
        style={styles.input}
        value={password}
        onChangeText={ t => setPassword(t) }
        placeholder="Password"
        secureTextEntry
      />
      <Button 
        title="Login"
        onPress={() => sendLogin(login, password)}
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