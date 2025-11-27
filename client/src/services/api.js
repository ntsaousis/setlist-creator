/**
 * API Service for backend communication
 */
import axios from 'axios';
import { getAccessToken } from './spotifyAuth';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

/**
 * Creates axios instance with auth header
 */
const createApiClient = () => {
  const token = getAccessToken();
  return axios.create({
    baseURL: API_BASE_URL,
    headers: {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` })
    }
  });
};

/**
 * Search for setlists by artist name
 */
export const searchSetlistsByArtist = async (artistName, page = 1) => {
  const api = createApiClient();
  const response = await api.get(`/setlists/${encodeURIComponent(artistName)}`, {
    params: { page }
  });
  return response.data;
};

/**
 * Get first valid setlist for an artist
 */
export const getFirstValidSetlist = async (artistName) => {
  const api = createApiClient();
  const response = await api.get(`/setlists/first-set/${encodeURIComponent(artistName)}`);
  return response.data;
};

/**
 * Create a Spotify playlist from a setlist
 */
export const createPlaylistFromSetlist = async (artistName, playlistName) => {
  const api = createApiClient();
  const response = await api.post('/spotify/create-playlist', {
    artistName,
    playlistName: playlistName || `${artistName} - Setlist`
  });
  return response.data;
};

/**
 * Get user's playlists
 */
export const getUserPlaylists = async () => {
  const api = createApiClient();
  const response = await api.get('/spotify/playlists');
  return response.data;
};
