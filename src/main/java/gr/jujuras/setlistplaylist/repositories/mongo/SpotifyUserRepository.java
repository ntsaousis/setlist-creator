package gr.jujuras.setlistplaylist.repositories.mongo;

import gr.jujuras.setlistplaylist.model.documents.SpotifyUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * MongoDB repository for SpotifyUser documents.
 * Provides database operations for managing Spotify user profiles.
 *
 * @author Setlist-Playlist Team
 * @version 1.0
 */
@Repository
public interface SpotifyUserRepository extends MongoRepository<SpotifyUser, String> {
    /**
     * Finds a Spotify user by their Spotify user ID.
     *
     * @param spotifyUserId the Spotify user ID
     * @return Optional containing the user if found
     */
    Optional<SpotifyUser> findBySpotifyUserId(String spotifyUserId);
}
