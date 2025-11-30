import React from 'react';
import { isAuthenticated, logout } from '../services/spotifyAuth';

/**
 * Header component with app branding and optional logout button
 */
const Header: React.FC = () => {
  const authenticated = isAuthenticated();

  return (
    <header className="app-header">
      <div className="header-content">
        <div className="header-brand">
          <h1>🎵 Setlist Playlist</h1>
          <p className="tagline">Create Spotify playlists from artist setlists</p>
        </div>
        {authenticated && (
          <button className="logout-btn" onClick={logout}>
            Logout
          </button>
        )}
      </div>
    </header>
  );
};

export default Header;
