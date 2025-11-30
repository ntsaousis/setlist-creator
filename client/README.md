# Setlist Playlist Client

A simple React app that allows users to create Spotify playlists from artist setlists.

## Features

- 🎵 OAuth2 login with Spotify
- 🔍 Search for artist setlists
- 📋 View setlist songs
- 🎶 Create Spotify playlists automatically

## Setup

### 1. Install Dependencies

```bash
cd client
npm install
```

### 2. Configure Environment Variables

Copy `.env.example` to `.env`:

```bash
cp .env.example .env
```

Edit `.env` and add your Spotify credentials:

```env
REACT_APP_SPOTIFY_CLIENT_ID=your_spotify_client_id
REACT_APP_REDIRECT_URI=http://localhost:3000/callback
REACT_APP_API_URL=http://localhost:8080/api
```

### 3. Configure Spotify App

1. Go to https://developer.spotify.com/dashboard
2. Create or select your app
3. Click "Edit Settings"
4. Add to "Redirect URIs": `http://localhost:3000/callback`
5. Save

### 4. Start the Development Server

```bash
npm start
```

The app will open at http://localhost:3000

## Backend Requirements

Make sure your Spring Boot backend is running on port 8080 with the following endpoints:

- `GET /api/setlists/first-set/{artistName}` - Get first valid setlist
- `POST /api/spotify/create-playlist` - Create Spotify playlist

## Usage

1. Click "Login with Spotify"
2. Authorize the app
3. Enter an artist name (e.g., "Metallica")
4. Click "Search"
5. Review the setlist songs
6. Click "Create Spotify Playlist"
7. Open the playlist in Spotify!

## Technologies

- React 18
- Axios for API calls
- Spotify Web API
- OAuth2 Implicit Grant Flow

## Project Structure

```
client/
├── public/
│   └── index.html
├── src/
│   ├── components/
│   │   ├── Login.js
│   │   └── ArtistSearch.js
│   ├── services/
│   │   ├── spotifyAuth.js
│   │   └── api.js
│   ├── App.js
│   ├── App.css
│   ├── index.js
│   └── index.css
├── package.json
└── README.md
```

## Notes

- Access tokens are stored in sessionStorage
- Tokens expire after 1 hour
- Free Spotify accounts work fine!
