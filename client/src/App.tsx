import React, { useEffect, useState } from 'react';
import Header from './components/Header';
import Footer from './components/Footer';
import Login from './components/Login';
import ArtistSearch from './components/ArtistSearch';
import { handleCallback, isAuthenticated } from './services/spotifyAuth';
import { authenticateUser } from './services/api';
import './App.css';

/**
 * Main App component
 * Handles authentication state and routing
 */
const App: React.FC = () => {
  const [authenticated, setAuthenticated] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const initAuth = async (): Promise<void> => {
      console.log('Initializing authentication...');
      console.log('Current URL:', window.location.href);

      // Check for authorization code in URL (OAuth callback)
      const token = await handleCallback();
      console.log('Token from callback:', token ? 'Received' : 'Not received');

      if (token) {
        console.log('Setting authenticated to true');

        // Authenticate user and store in database
        try {
          const user = await authenticateUser(token);
          console.log('User authenticated and stored:', user.displayName);
        } catch (error) {
          console.error('Failed to authenticate user:', error);
          // Continue anyway - the user is still authenticated with Spotify
        }

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
      <Header />
      <main className="main-content">
        {authenticated ? <ArtistSearch /> : <Login />}
      </main>
      <Footer />
    </div>
  );
};

export default App;
