/**
 * API Service for backend communication
 */
import axios, { AxiosInstance } from 'axios';
import { getAccessToken } from './spotifyAuth';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

export interface Setlist {
  artistName: string;
  songs: string[];
}

export interface PlaylistResponse {
  playlistName: string;
  trackUris: string[];
  spotifyPlaylistId: string;
}

export interface SpotifyUser {
  id: string;
  spotifyUserId: string;
  displayName: string;
  email: string;
  country: string;
  product: string;
  firstLoginAt: string;
  lastLoginAt: string;
}

/**
 * Creates axios instance with auth header
 */
const createApiClient = (): AxiosInstance => {
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
export const searchSetlistsByArtist = async (artistName: string, page: number = 1): Promise<any> => {
  const api = createApiClient();
  const response = await api.get(`/setlists/${encodeURIComponent(artistName)}`, {
    params: { page }
  });
  return response.data;
};

/**
 * Get first valid setlist for an artist
 */
export const getFirstValidSetlist = async (artistName: string): Promise<Setlist> => {
  const api = createApiClient();
  const response = await api.get<Setlist>(`/setlists/first-set/${encodeURIComponent(artistName)}`);
  return response.data;
};

/**
 * Create a Spotify playlist from a setlist
 */
export const createPlaylistFromSetlist = async (
  artistName: string,
  playlistName: string
): Promise<PlaylistResponse> => {
  const api = createApiClient();
  const response = await api.post<PlaylistResponse>('/spotify/create-playlist', {
    artistName,
    playlistName: playlistName || `${artistName} - Setlist`
  });
  return response.data;
};

/**
 * Get user's playlists
 */
export const getUserPlaylists = async (): Promise<any> => {
  const api = createApiClient();
  const response = await api.get('/spotify/playlists');
  return response.data;
};

/**
 * Authenticate user and store in database
 * Should be called after user logs in with Spotify
 */
export const authenticateUser = async (accessToken: string): Promise<SpotifyUser> => {
  const response = await axios.post<SpotifyUser>(
    `${API_BASE_URL}/spotify/authenticate`,
    {},
    {
      headers: {
        'Authorization': `Bearer ${accessToken}`
      }
    }
  );
  return response.data;
};
