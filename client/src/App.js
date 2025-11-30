import React, { useEffect, useState } from 'react';
import Login from './components/Login';
import ArtistSearch from './components/ArtistSearch';
import { handleCallback, isAuthenticated } from './services/spotifyAuth';
import './App.css';

/**
 * Main App component
 * Handles authentication state and routing
 */
function App() {
  const [authenticated, setAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initAuth = async () => {
      console.log('Initializing authentication...');
      console.log('Current URL:', window.location.href);

      // Check for authorization code in URL (OAuth callback)
      const token = await handleCallback();
      console.log('Token from callback:', token ? 'Received' : 'Not received');

      if (token) {
        console.log('Setting authenticated to true');
        setAuthenticated(true);
      } else {
        // Check if already authenticated
        const isAuth = isAuthenticated();
        console.log('Already authenticated:', isAuth);
        setAuthenticated(isAuth);
      }

      setLoading(false);
    };

    initAuth();
  }, []);

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Loading...</p>
      </div>
    );
  }

  return (
    <div className="App">
      {authenticated ? <ArtistSearch /> : <Login />}
    </div>
  );
}

export default App;
