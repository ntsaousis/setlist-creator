/**
 * Spotify OAuth2 Authentication Service
 * Uses Authorization Code Flow with PKCE (Proof Key for Code Exchange)
 */

const CLIENT_ID = process.env.REACT_APP_SPOTIFY_CLIENT_ID;
const REDIRECT_URI = process.env.REACT_APP_REDIRECT_URI || 'http://127.0.0.1:3000/callback';
const AUTH_ENDPOINT = 'https://accounts.spotify.com/authorize';
const TOKEN_ENDPOINT = 'https://accounts.spotify.com/api/token';
const SCOPES = [
  'user-read-private',
  'user-read-email',
  'playlist-modify-public',
  'playlist-modify-private'
];

/**
 * Generates a random code verifier for PKCE
 */
const generateCodeVerifier = () => {
  const array = new Uint8Array(32);
  crypto.getRandomValues(array);
  return base64URLEncode(array);
};

/**
 * Generates code challenge from verifier
 */
const generateCodeChallenge = async (verifier) => {
  const encoder = new TextEncoder();
  const data = encoder.encode(verifier);
  const hash = await crypto.subtle.digest('SHA-256', data);
  return base64URLEncode(new Uint8Array(hash));
};

/**
 * Base64 URL encoding (without padding)
 */
const base64URLEncode = (buffer) => {
  return btoa(String.fromCharCode(...buffer))
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=+$/, '');
};

/**
 * Redirects user to Spotify authorization page
 */
export const loginWithSpotify = async () => {
  // Generate and store code verifier
  const codeVerifier = generateCodeVerifier();
  sessionStorage.setItem('spotify_code_verifier', codeVerifier);

  // Generate code challenge
  const codeChallenge = await generateCodeChallenge(codeVerifier);

  const params = new URLSearchParams({
    client_id: CLIENT_ID,
    response_type: 'code',
    redirect_uri: REDIRECT_URI,
    scope: SCOPES.join(' '),
    code_challenge_method: 'S256',
    code_challenge: codeChallenge,
    show_dialog: true
  });

  window.location.href = `${AUTH_ENDPOINT}?${params.toString()}`;
};

/**
 * Exchanges authorization code for access token
 */
export const handleCallback = async () => {
  console.log('handleCallback: Starting...');
  const params = new URLSearchParams(window.location.search);
  const code = params.get('code');
  const error = params.get('error');

  console.log('handleCallback: Code present:', !!code);
  console.log('handleCallback: Error present:', !!error);

  if (error) {
    console.error('Spotify authorization error:', error);
    return null;
  }

  if (!code) {
    console.log('handleCallback: No code in URL, skipping token exchange');
    return null;
  }

  // Get stored code verifier
  const codeVerifier = sessionStorage.getItem('spotify_code_verifier');
  console.log('handleCallback: Code verifier present:', !!codeVerifier);

  if (!codeVerifier) {
    console.error('Code verifier not found');
    return null;
  }

  try {
    console.log('handleCallback: Exchanging code for token...');

    // Exchange code for token
    const response = await fetch(TOKEN_ENDPOINT, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      body: new URLSearchParams({
        client_id: CLIENT_ID,
        grant_type: 'authorization_code',
        code: code,
        redirect_uri: REDIRECT_URI,
        code_verifier: codeVerifier
      })
    });

    console.log('handleCallback: Response status:', response.status);

    if (!response.ok) {
      const errorData = await response.json();
      console.error('Token exchange failed:', errorData);
      throw new Error('Failed to exchange code for token');
    }

    const data = await response.json();
    const accessToken = data.access_token;
    const expiresIn = data.expires_in;

    console.log('handleCallback: Token received, expires in', expiresIn, 'seconds');

    // Store token
    sessionStorage.setItem('spotify_access_token', accessToken);
    sessionStorage.setItem('spotify_token_expires', Date.now() + (expiresIn * 1000));

    // Clean up
    sessionStorage.removeItem('spotify_code_verifier');
    window.history.replaceState({}, document.title, '/');

    console.log('handleCallback: Success!');
    return accessToken;
  } catch (error) {
    console.error('Error exchanging code for token:', error);
    return null;
  }
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
  sessionStorage.removeItem('spotify_code_verifier');
  window.location.href = '/';
};

/**
 * Checks if user is authenticated
 */
export const isAuthenticated = () => {
  return getAccessToken() !== null;
};
