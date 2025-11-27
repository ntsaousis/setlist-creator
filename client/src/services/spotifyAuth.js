/**
 * Spotify OAuth2 Authentication Service
 */

const CLIENT_ID = process.env.REACT_APP_SPOTIFY_CLIENT_ID;
const REDIRECT_URI = process.env.REACT_APP_REDIRECT_URI || 'http://localhost:3000/callback';
const AUTH_ENDPOINT = 'https://accounts.spotify.com/authorize';
const SCOPES = [
  'user-read-private',
  'user-read-email',
  'playlist-modify-public',
  'playlist-modify-private'
];

/**
 * Redirects user to Spotify authorization page
 */
export const loginWithSpotify = () => {
  const params = new URLSearchParams({
    client_id: CLIENT_ID,
    response_type: 'token',
    redirect_uri: REDIRECT_URI,
    scope: SCOPES.join(' '),
    show_dialog: true
  });

  window.location.href = `${AUTH_ENDPOINT}?${params.toString()}`;
};

/**
 * Extracts access token from URL hash (after OAuth callback)
 */
export const getAccessTokenFromUrl = () => {
  const hash = window.location.hash;
  if (!hash) return null;

  const params = new URLSearchParams(hash.substring(1));
  const accessToken = params.get('access_token');
  const expiresIn = params.get('expires_in');

  if (accessToken) {
    // Store token in sessionStorage
    sessionStorage.setItem('spotify_access_token', accessToken);
    sessionStorage.setItem('spotify_token_expires', Date.now() + (expiresIn * 1000));

    // Clear hash from URL
    window.location.hash = '';

    return accessToken;
  }

  return null;
};

/**
 * Gets stored access token
 */
export const getAccessToken = () => {
  const token = sessionStorage.getItem('spotify_access_token');
  const expires = sessionStorage.getItem('spotify_token_expires');

  if (token && expires && Date.now() < parseInt(expires)) {
    return token;
  }

  // Token expired
  sessionStorage.removeItem('spotify_access_token');
  sessionStorage.removeItem('spotify_token_expires');
  return null;
};

/**
 * Logs out by clearing stored token
 */
export const logout = () => {
  sessionStorage.removeItem('spotify_access_token');
  sessionStorage.removeItem('spotify_token_expires');
  window.location.href = '/';
};

/**
 * Checks if user is authenticated
 */
export const isAuthenticated = () => {
  return getAccessToken() !== null;
};
