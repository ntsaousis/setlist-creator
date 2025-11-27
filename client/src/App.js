import React, { useEffect, useState } from 'react';
import Login from './components/Login';
import ArtistSearch from './components/ArtistSearch';
import { getAccessTokenFromUrl, isAuthenticated } from './services/spotifyAuth';
import './App.css';

/**
 * Main App component
 * Handles authentication state and routing
 */
function App() {
  const [authenticated, setAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check for access token in URL (OAuth callback)
    const tokenFromUrl = getAccessTokenFromUrl();

    if (tokenFromUrl) {
      setAuthenticated(true);
    } else {
      // Check if already authenticated
      setAuthenticated(isAuthenticated());
    }

    setLoading(false);
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
