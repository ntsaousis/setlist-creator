package gr.jujuras.setlistplaylist.model.documents;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

/**
 * MongoDB document representing a Spotify user.
 * Stores user profile information obtained from Spotify's /me endpoint.
 *
 * @author Setlist-Playlist Team
 * @version 1.0
 */
@Document("spotify-users")
@Getter
@Setter
public class SpotifyUser {
    @Id
    private String id;

    @Indexed(unique = true)
    private String spotifyUserId;  // Spotify's user ID

    private String displayName;
    private String email;
    private String country;
    private String product;  // premium, free, etc.

    private LocalDateTime firstLoginAt;
    private LocalDateTime lastLoginAt;
}
