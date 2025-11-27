package gr.jujuras.setlistplaylist.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.jujuras.setlistplaylist.core.exceptions.ExternalApiException;
import gr.jujuras.setlistplaylist.dto.spotify.*;
import gr.jujuras.setlistplaylist.model.documents.SpotifyPlaylist;
import gr.jujuras.setlistplaylist.repositories.mongo.SpotifyPlaylistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for interacting with the Spotify Web API.
 * Provides functionality for creating playlists, searching tracks,
 * and managing user's Spotify data.
 *
 * @author Setlist-Playlist Team
 * @version 1.0
 */
@Service
public class SpotifyService {

    private static final Logger logger = LoggerFactory.getLogger(SpotifyService.class);
    private static final String SPOTIFY_API_BASE_URL = "https://api.spotify.com/v1";

    private final SpotifyPlaylistRepository playlistRepository;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${spotify.default-market:US}")
    private String defaultMarket;

    /**
     * Constructs a new SpotifyService with required dependencies.
     *
     * @param playlistRepository repository for storing playlist data
     * @param restClient REST client for making HTTP requests
     * @param objectMapper Jackson ObjectMapper for JSON processing
     */
    public SpotifyService(SpotifyPlaylistRepository playlistRepository,
                          RestClient restClient,
                          ObjectMapper objectMapper) {
        this.playlistRepository = playlistRepository;
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Retrieves the authenticated user's Spotify profile.
     *
     * @param accessToken the OAuth2 access token
     * @return SpotifyUserProfileDTO containing user profile information
     * @throws ExternalApiException if the API request fails
     */
    public SpotifyUserProfileDTO getUserProfile(String accessToken) {
        logger.debug("Fetching user profile from Spotify");

        try {
            SpotifyUserProfileDTO profile = restClient.get()
                    .uri(SPOTIFY_API_BASE_URL + "/me")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .body(SpotifyUserProfileDTO.class);

            if (profile != null) {
                logger.info("Successfully retrieved profile for user: {}", profile.getId());
                return profile;
            }

            throw new ExternalApiException("Spotify returned null profile");
        } catch (Exception e) {
            logger.error("Error fetching user profile from Spotify: {}", e.getMessage(), e);
            throw new ExternalApiException("Failed to fetch user profile from Spotify", e);
        }
    }

    /**
     * Searches for a track on Spotify by song name and artist.
     * Returns the best match based on popularity and relevance.
     *
     * @param songName the name of the song to search for
     * @param artistName the name of the artist
     * @param accessToken the OAuth2 access token
     * @return SpotifyTrackDTO of the best matching track, or null if not found
     * @throws ExternalApiException if the API request fails
     */
    public SpotifyTrackDTO searchTrack(String songName, String artistName, String accessToken) {
        logger.debug("Searching for track: '{}' by '{}'", songName, artistName);

        try {
            // Build a search query: "track:songName artist:artistName"
            String query = String.format("track:%s artist:%s", songName, artistName);

            SpotifyTrackSearchResponseDTO response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api.spotify.com")
                            .path("/v1/search")
                            .queryParam("q", query)
                            .queryParam("type", "track")
                            .queryParam("limit", 5)
                            .queryParam("market", defaultMarket)
                            .build())
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .body(SpotifyTrackSearchResponseDTO.class);

            if (response != null && response.getTracks() != null &&
                !response.getTracks().getItems().isEmpty()) {

                SpotifyTrackDTO bestMatch = response.getTracks().getItems().get(0);
                logger.info("Found track: '{}' by '{}' (URI: {})",
                        bestMatch.getName(),
                        bestMatch.getArtists().get(0).getName(),
                        bestMatch.getUri());
                return bestMatch;
            }

            logger.warn("No tracks found for: '{}' by '{}'", songName, artistName);
            return null;
        } catch (Exception e) {
            logger.error("Error searching for track '{}' by '{}': {}",
                    songName, artistName, e.getMessage(), e);
            throw new ExternalApiException("Failed to search track on Spotify", e);
        }
    }

    /**
     * Searches for multiple tracks on Spotify.
     * Returns only the tracks that were successfully found.
     *
     * @param songNames list of song names to search for
     * @param artistName the name of the artist for all songs
     * @param accessToken the OAuth2 access token
     * @return list of found SpotifyTrackDTOs (may be smaller than input list)
     */
    public List<SpotifyTrackDTO> searchTracks(List<String> songNames,
                                               String artistName,
                                               String accessToken) {
        logger.info("Searching for {} tracks by artist '{}'", songNames.size(), artistName);

        List<SpotifyTrackDTO> foundTracks = new ArrayList<>();
        int notFoundCount = 0;

        for (String songName : songNames) {
            try {
                SpotifyTrackDTO track = searchTrack(songName, artistName, accessToken);
                if (track != null) {
                    foundTracks.add(track);
                } else {
                    notFoundCount++;
                }
            } catch (Exception e) {
                logger.warn("Failed to search for track '{}': {}", songName, e.getMessage());
                notFoundCount++;
            }
        }

        logger.info("Found {}/{} tracks ({} not found)",
                foundTracks.size(), songNames.size(), notFoundCount);
        return foundTracks;
    }

    /**
     * Creates a new playlist on the user's Spotify account.
     *
     * @param userId the Spotify user ID
     * @param playlistName the name for the new playlist
     * @param description the description for the playlist
     * @param isPublic whether the playlist should be public
     * @param accessToken the OAuth2 access token
     * @return PlaylistResponseDTO containing the created playlist information
     * @throws ExternalApiException if the API request fails
     */
    public PlaylistResponseDTO createPlaylist(String userId,
                                               String playlistName,
                                               String description,
                                               boolean isPublic,
                                               String accessToken) {
        logger.info("Creating playlist '{}' for user '{}'", playlistName, userId);

        try {
            CreatePlaylistRequestDTO request = new CreatePlaylistRequestDTO(
                    playlistName,
                    description,
                    isPublic
            );

            PlaylistResponseDTO response = restClient.post()
                    .uri(SPOTIFY_API_BASE_URL + "/users/" + userId + "/playlists")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(PlaylistResponseDTO.class);

            if (response != null) {
                logger.info("Successfully created playlist '{}' with ID: {}",
                        playlistName, response.getId());
                return response;
            }

            throw new ExternalApiException("Spotify returned null playlist response");
        } catch (Exception e) {
            logger.error("Error creating playlist '{}': {}", playlistName, e.getMessage(), e);
            throw new ExternalApiException("Failed to create playlist on Spotify", e);
        }
    }

    /**
     * Adds tracks to an existing Spotify playlist.
     *
     * @param playlistId the Spotify playlist ID
     * @param trackUris list of Spotify track URIs to add
     * @param accessToken the OAuth2 access token
     * @throws ExternalApiException if the API request fails
     */
    public void addTracksToPlaylist(String playlistId,
                                     List<String> trackUris,
                                     String accessToken) {
        if (trackUris == null || trackUris.isEmpty()) {
            logger.warn("No tracks to add to playlist {}", playlistId);
            return;
        }

        logger.info("Adding {} tracks to playlist {}", trackUris.size(), playlistId);

        try {
            // Spotify allows max 100 tracks per request, batch if necessary
            int batchSize = 100;
            for (int i = 0; i < trackUris.size(); i += batchSize) {
                List<String> batch = trackUris.subList(
                        i,
                        Math.min(i + batchSize, trackUris.size())
                );

                Map<String, List<String>> requestBody = new HashMap<>();
                requestBody.put("uris", batch);

                restClient.post()
                        .uri(SPOTIFY_API_BASE_URL + "/playlists/" + playlistId + "/tracks")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(requestBody)
                        .retrieve()
                        .toBodilessEntity();

                logger.debug("Added batch of {} tracks to playlist", batch.size());
            }

            logger.info("Successfully added {} tracks to playlist {}",
                    trackUris.size(), playlistId);
        } catch (Exception e) {
            logger.error("Error adding tracks to playlist {}: {}",
                    playlistId, e.getMessage(), e);
            throw new ExternalApiException("Failed to add tracks to playlist", e);
        }
    }

    /**
     * Creates a complete Spotify playlist from a list of songs.
     * This is the main high-level method that orchestrates the entire process:
     * 1. Gets user profile
     * 2. Searches for all tracks
     * 3. Creates playlist
     * 4. Adds tracks to the playlist
     * 5. Saves playlist data to MongoDB
     *
     * @param artistName the name of the artist
     * @param songNames list of song names to add to the playlist
     * @param playlistName the name for the new playlist
     * @param accessToken the OAuth2 access token
     * @return SpotifyPlaylist the saved playlist document
     * @throws ExternalApiException if any API operation fails
     */
    public SpotifyPlaylist createPlaylistFromSetlist(String artistName,
                                                      List<String> songNames,
                                                      String playlistName,
                                                      String accessToken) {
        logger.info("Creating playlist '{}' from setlist with {} songs by '{}'",
                playlistName, songNames.size(), artistName);

        try {
            // Step 1: Get user profile
            SpotifyUserProfileDTO userProfile = getUserProfile(accessToken);
            String userId = userProfile.getId();

            // Step 2: Search for tracks
            List<SpotifyTrackDTO> foundTracks = searchTracks(songNames, artistName, accessToken);

            if (foundTracks.isEmpty()) {
                throw new ExternalApiException("No tracks found on Spotify for the setlist");
            }

            List<String> trackUris = foundTracks.stream()
                    .map(SpotifyTrackDTO::getUri)
                    .collect(Collectors.toList());

            // Step 3: Creates a playlist
            String description = String.format("Setlist playlist for %s - Created by Setlist-Playlist",
                    artistName);
            PlaylistResponseDTO playlistResponse = createPlaylist(
                    userId,
                    playlistName,
                    description,
                    false, // private by default
                    accessToken
            );

            // Step 4: Add tracks to the playlist
            addTracksToPlaylist(playlistResponse.getId(), trackUris, accessToken);

            // Step 5: Save to MongoDB
            SpotifyPlaylist playlist = new SpotifyPlaylist();
            playlist.setPlaylistName(playlistName);
            playlist.setSpotifyPlaylistId(playlistResponse.getId());
            playlist.setSpotifyUserId(userId);
            playlist.setArtistName(artistName);
            playlist.setTrackUris(trackUris);
            playlist.setCreatedAt(LocalDateTime.now());

            // Try to save it to MongoDB, but don't fail if MongoDB is unavailable
            try {
                SpotifyPlaylist savedPlaylist = playlistRepository.save(playlist);
                logger.info("Successfully created and saved playlist '{}' with {} tracks (ID: {})",
                        playlistName, trackUris.size(), savedPlaylist.getId());
                return savedPlaylist;
            } catch (Exception mongoException) {
                logger.warn("Playlist created on Spotify but failed to save to MongoDB: {}",
                        mongoException.getMessage());
                logger.info("Successfully created playlist '{}' with {} tracks on Spotify (ID: {})",
                        playlistName, trackUris.size(), playlistResponse.getId());
                // Return the playlist object even though it's not saved to DB
                return playlist;
            }
        } catch (ExternalApiException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error creating playlist from setlist: {}",
                    e.getMessage(), e);
            throw new ExternalApiException("Failed to create playlist from setlist", e);
        }
    }

    /**
     * Retrieves all playlists for a specific Spotify user from the database.
     *
     * @param spotifyUserId the Spotify user ID
     * @return list of SpotifyPlaylist documents
     */
    public List<SpotifyPlaylist> getPlaylistsByUserId(String spotifyUserId) {
        logger.debug("Fetching playlists for user: {}", spotifyUserId);
        List<SpotifyPlaylist> playlists = playlistRepository.findBySpotifyUserId(spotifyUserId);
        logger.info("Found {} playlists for user {}", playlists.size(), spotifyUserId);
        return playlists;
    }

    /**
     * Saves a playlist document to MongoDB.
     * This is a lower-level method for manual playlist saves.
     *
     * @param playlist the SpotifyPlaylist to save
     * @return the saved SpotifyPlaylist with generated ID
     */
    public SpotifyPlaylist savePlaylist(SpotifyPlaylist playlist) {
        logger.info("Saving playlist '{}' to database", playlist.getPlaylistName());
        SpotifyPlaylist saved = playlistRepository.save(playlist);
        logger.debug("Playlist saved with ID: {}", saved.getId());
        return saved;
    }
}
