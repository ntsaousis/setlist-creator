# 🎵 Setlist Playlist

Convert artist setlists into Spotify playlists automatically. Search for any artist, find their recent setlists, and create a Spotify playlist with one click.

## ✨ Features

- 🔍 **Artist Search** - Search for any artist's setlists
- 📋 **Setlist Discovery** - Automatically find the most recent valid setlist
- 🎶 **Playlist Creation** - Create Spotify playlists from setlists instantly
- 🔐 **Spotify OAuth** - Secure authentication using Spotify's OAuth2 PKCE flow
- 🎨 **Spotify-themed UI** - Dark theme matching Spotify's desktop app design
- 📱 **Responsive Design** - Works seamlessly on desktop and mobile devices

## 🛠️ Tech Stack

### Backend
- **Java 17+** with Spring Boot
- **Spring Security** - OAuth2 authentication
- **MongoDB** - Database for playlist storage
- **Setlist.fm API** - Setlist data source
- **Spotify Web API** - Playlist creation and management

### Frontend
- **React 18** with TypeScript
- **Tailwind CSS** - Utility-first CSS framework
- **Roboto Font** - Modern, clean typography
- **Axios** - HTTP client for API requests

## 📋 Prerequisites

- **Java Development Kit (JDK) 17+**
- **Node.js 16+** and npm
- **MongoDB** instance (local or cloud)
- **Spotify Developer Account** - [Create one here](https://developer.spotify.com/)
- **Setlist.fm API Key** - [Register here](https://api.setlist.fm/docs/1.0/index.html)

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/setlist-playlist.git
cd setlist-playlist
```

### 2. Backend Setup

#### Configure Application Properties

Create `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/setlist-playlist

# Spotify API Credentials
spotify.client.id=YOUR_SPOTIFY_CLIENT_ID
spotify.client.secret=YOUR_SPOTIFY_CLIENT_SECRET
spotify.redirect.uri=http://localhost:8080/api/oauth2/callback

# Setlist.fm API
setlistfm.api.key=YOUR_SETLISTFM_API_KEY
setlistfm.api.url=https://api.setlist.fm/rest/1.0

# CORS Configuration
cors.allowed.origins=http://localhost:3000
```

#### Build and Run the Backend

```bash
./gradlew bootRun
```

The backend will start on `http://localhost:8080`

### 3. Frontend Setup

#### Navigate to Client Directory

```bash
cd client
```

#### Install Dependencies

```bash
npm install
```

#### Configure Environment Variables

Create `.env` in the `client` directory:

```env
REACT_APP_SPOTIFY_CLIENT_ID=YOUR_SPOTIFY_CLIENT_ID
REACT_APP_REDIRECT_URI=http://127.0.0.1:3000/callback
REACT_APP_API_URL=http://localhost:8080/api
```

#### Start the Development Server

```bash
npm start
```

The frontend will start on `http://localhost:3000`

## 🔑 Spotify API Setup

1. Go to [Spotify Developer Dashboard](https://developer.spotify.com/dashboard)
2. Create a new application
3. Add redirect URI: `http://127.0.0.1:3000/callback`
4. Copy your **Client ID** and **Client Secret**
5. Add these to your configuration files

## 📁 Project Structure

```
setlist-playlist/
├── src/main/java/gr/jujuras/setlistplaylist/
│   ├── config/              # Spring configuration
│   ├── controllers/         # REST API endpoints
│   ├── core/                # Core business logic & exceptions
│   ├── dto/                 # Data Transfer Objects
│   ├── model/               # Domain models
│   ├── repositories/        # MongoDB repositories
│   ├── security/            # Security configuration
│   └── services/            # Business logic services
├── client/
│   ├── public/              # Static files
│   └── src/
│       ├── components/      # React components
│       ├── services/        # API & auth services
│       ├── App.tsx          # Main app component
│       └── index.tsx        # Entry point
├── build.gradle             # Gradle build configuration
└── README.md               # Project documentation
```

## 🎯 API Endpoints

### Authentication
- `GET /api/oauth2/authorize` - Initiate Spotify OAuth flow
- `GET /api/oauth2/callback` - OAuth callback handler

### Setlists
- `GET /api/setlists/{artistName}` - Get setlists by artist name
- `GET /api/setlists/first-set/{artistName}` - Get first valid setlist

### Spotify
- `POST /api/spotify/create-playlist` - Create playlist from setlist
- `GET /api/spotify/playlists` - Get user's playlists

## 🎨 UI Features

- **Spotify-themed Design** - Dark background with green accents
- **Roboto Typography** - Clean, modern font throughout
- **Responsive Layout** - Mobile-first design with Tailwind CSS
- **Smooth Animations** - Hover effects and transitions
- **Loading States** - Clear feedback during async operations

## 🔒 Security

- **OAuth 2.0 PKCE** - Secure authorization code flow with PKCE
- **Session Storage** - Secure token storage in browser
- **CORS Protection** - Configured allowed origins
- **Environment Variables** - Sensitive data kept out of code

## 🧪 Development

### Run Backend Tests

```bash
./gradlew test
```

### Run Frontend in Development Mode

```bash
cd client
npm start
```

### Build for Production

**Backend:**
```bash
./gradlew build
java -jar build/libs/setlist-playlist-*.jar
```

**Frontend:**
```bash
cd client
npm run build
```

## 🐛 Troubleshooting

### Common Issues

**MongoDB Connection Error**
- Ensure MongoDB is running: `mongod`
- Check connection string in `application.properties`

**Spotify Authentication Fails**
- Verify redirect URI matches exactly in Spotify Dashboard
- Check Client ID and Secret are correct

**CORS Errors**
- Ensure frontend URL is in `cors.allowed.origins`
- Check that both servers are running

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 👤 Author

ntsaousis - [Facebook](https://facebook.com/nick.chau.5)

Project Link: [https://github.com/ntsaousis/setlist-playlist](https://github.com/ntsaousis/setlist-playlist)

## 🙏 Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot) - Backend framework
- [Setlist.fm](https://www.setlist.fm/) - Setlist data provider
- [Spotify Web API](https://developer.spotify.com/documentation/web-api/) - Music platform integration
- [Tailwind CSS](https://tailwindcss.com/) - CSS framework
- [React](https://reactjs.org/) - Frontend framework

---

**Made with ❤️ and TypeScript**
