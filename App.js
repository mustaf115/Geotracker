import { createAppContainer } from 'react-navigation';
import { createStackNavigator } from 'react-navigation-stack';
import LoginScreen from './screens/LoginScreen';
import MapScreen from './screens/MapScreen';

const MainNavigator = createStackNavigator({
  Login: {
    screen: LoginScreen
  },
  Map: {
    screen: MapScreen
  }
},
{
  headerMode: 'none',
  initialRouteName: 'Map'
}
);

const App = createAppContainer(MainNavigator);

export default App;