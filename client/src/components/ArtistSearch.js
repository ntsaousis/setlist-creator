import React, { useState } from 'react';
import { getFirstValidSetlist, createPlaylistFromSetlist } from '../services/api';
import { logout } from '../services/spotifyAuth';

/**
 * Artist search and playlist creation component
 */
const ArtistSearch = () => {
  const [artistName, setArtistName] = useState('');
  const [setlist, setSetlist] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [createdPlaylist, setCreatedPlaylist] = useState(null);
  const [creating, setCreating] = useState(false);

  /**
   * Searches for artist's first valid setlist
   */
  const handleSearch = async (e) => {
    e.preventDefault();

    if (!artistName.trim()) {
      setError('Please enter an artist name');
      return;
    }

    setLoading(true);
    setError(null);
    setSetlist(null);
    setCreatedPlaylist(null);

    try {
      const result = await getFirstValidSetlist(artistName);
      setSetlist(result);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to find setlist. Try another artist.');
      console.error('Search error:', err);
    } finally {
      setLoading(false);
    }
  };

  /**
   * Creates Spotify playlist from the found setlist
   */
  const handleCreatePlaylist = async () => {
    if (!setlist) return;

    setCreating(true);
    setError(null);

    try {
      const playlistName = `${setlist.artistName} - Live Setlist`;
      const result = await createPlaylistFromSetlist(setlist.artistName, playlistName);
      setCreatedPlaylist(result);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create playlist. Please try again.');
      console.error('Playlist creation error:', err);
    } finally {
      setCreating(false);
    }
  };

  return (
    <div className="search-container">
      <div className="header">
        <h1>🎵 Setlist Playlist</h1>
        <button className="logout-btn" onClick={logout}>
          Logout
        </button>
      </div>

      <div className="search-card">
        <form onSubmit={handleSearch}>
          <div className="search-box">
            <input
              type="text"
              placeholder="Enter artist name (e.g., Metallica, Coldplay)"
              value={artistName}
              onChange={(e) => setArtistName(e.target.value)}
              disabled={loading}
              className="search-input"
            />
            <button
              type="submit"
              disabled={loading}
              className="search-btn"
            >
              {loading ? 'Searching...' : 'Search'}
            </button>
          </div>
        </form>

        {error && (
          <div className="error-message">
            ⚠️ {error}
          </div>
        )}

        {setlist && (
          <div className="setlist-result">
            <h2>Found Setlist for {setlist.artistName}</h2>
            <p className="song-count">{setlist.songs.length} songs found</p>

            <div className="songs-list">
              {setlist.songs.map((song, index) => (
                <div key={index} className="song-item">
                  <span className="song-number">{index + 1}</span>
                  <span className="song-name">{song}</span>
                </div>
              ))}
            </div>

            {!createdPlaylist && (
              <button
                onClick={handleCreatePlaylist}
                disabled={creating}
                className="create-playlist-btn"
              >
                {creating ? '🎶 Creating Playlist...' : '🎶 Create Spotify Playlist'}
              </button>
            )}
          </div>
        )}

        {createdPlaylist && (
          <div className="success-message">
            <h2>✅ Playlist Created!</h2>
            <p>
              <strong>{createdPlaylist.playlistName}</strong>
            </p>
            <p className="tracks-added">
              {createdPlaylist.trackUris.length} tracks added to your Spotify account
            </p>
            <a
              href={`https://open.spotify.com/playlist/${createdPlaylist.spotifyPlaylistId}`}
              target="_blank"
              rel="noopener noreferrer"
              className="open-spotify-btn"
            >
              Open in Spotify
            </a>
          </div>
        )}
      </div>
    </div>
  );
};

export default ArtistSearch;
