package gr.jujuras.setlistplaylist.controllers;

import gr.jujuras.setlistplaylist.dto.CreatePlaylistRequestDTO;
import gr.jujuras.setlistplaylist.dto.SetDTO;
import gr.jujuras.setlistplaylist.model.documents.SpotifyPlaylist;
import gr.jujuras.setlistplaylist.services.SetlistService;
import gr.jujuras.setlistplaylist.services.SpotifyService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Spotify-related operations.
 * Handles playlist creation and retrieval.
 *
 * @author Setlist-Playlist Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/spotify")
@RequiredArgsConstructor
public class SpotifyController {

    private static final Logger logger = LoggerFactory.getLogger(SpotifyController.class);

    private final SpotifyService spotifyService;
    private final SetlistService setlistService;

    /**
     * Creates a Spotify playlist from an artist's setlist.
     * This endpoint orchestrates the full workflow:
     * 1. Fetches the artist's first valid setlist from Setlist.fm
     * 2. Searches for tracks on Spotify
     * 3. Creates a playlist on the user's Spotify account
     * 4. Adds the found tracks to the playlist
     * 5. Saves playlist metadata to MongoDB
     *
     * @param request containing artistName and playlistName
     * @param authHeader the Authorization header with Bearer token
     * @return ResponseEntity with created SpotifyPlaylist
     */
    @PostMapping("/create-playlist")
    public ResponseEntity<SpotifyPlaylist> createPlaylistFromSetlist(
            @RequestBody CreatePlaylistRequestDTO request,
            @RequestHeader("Authorization") String authHeader) {

        logger.info("Received request to create playlist for artist: {}", request.getArtistName());

        // Extract access token from "Bearer xxx" header
        String accessToken = authHeader.replace("Bearer ", "").trim();

        // Get the first valid setlist for the artist
        SetDTO setlist = setlistService.getFirstValidSetByArtist(request.getArtistName());

        // Create playlist from setlist
        SpotifyPlaylist playlist = spotifyService.createPlaylistFromSetlist(
                setlist.artistName(),
                setlist.songs(),
                request.getPlaylistName(),
                accessToken
        );

        logger.info("Successfully created playlist '{}' with {} tracks",
                playlist.getPlaylistName(), playlist.getTrackUris().size());

        return new ResponseEntity<>(playlist, HttpStatus.CREATED);
    }

    /**
     * Retrieves all playlists for the authenticated user.
     *
     * @param authHeader the Authorization header with Bearer token
     * @return ResponseEntity with list of user's playlists
     */
    @GetMapping("/playlists")
    public ResponseEntity<List<SpotifyPlaylist>> getUserPlaylists(
            @RequestHeader("Authorization") String authHeader) {

        String accessToken = authHeader.replace("Bearer ", "").trim();

        // Get user profile to retrieve user ID
        var userProfile = spotifyService.getUserProfile(accessToken);

        // Fetch playlists from database
        List<SpotifyPlaylist> playlists = spotifyService.getPlaylistsByUserId(userProfile.getId());

        logger.info("Retrieved {} playlists for user {}", playlists.size(), userProfile.getId());

        return ResponseEntity.ok(playlists);
    }

    /**
     * Test endpoint for MongoDB connection.
     * Creates a simple test playlist to verify MongoDB is working.
     *
     * @return ResponseEntity with saved playlist or error
     */
    @PostMapping("/test-mongo")
    public ResponseEntity<?> testMongo() {
        try {
            logger.info("Testing MongoDB save...");

            SpotifyPlaylist testPlaylist = new SpotifyPlaylist();
            testPlaylist.setPlaylistName("Test Playlist");
            testPlaylist.setSpotifyPlaylistId("test_" + System.currentTimeMillis());
            testPlaylist.setSpotifyUserId("test_user");
            testPlaylist.setArtistName("Test Artist");
            testPlaylist.setTrackUris(java.util.Arrays.asList("spotify:track:test1", "spotify:track:test2"));
            testPlaylist.setCreatedAt(java.time.LocalDateTime.now());

            SpotifyPlaylist saved = spotifyService.savePlaylist(testPlaylist);

            logger.info("MongoDB test successful! Saved with ID: {}", saved.getId());
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            logger.error("MongoDB test failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("MongoDB save failed: " + e.getMessage());
        }
    }
}
