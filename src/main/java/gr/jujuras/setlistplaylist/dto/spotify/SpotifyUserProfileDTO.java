package gr.jujuras.setlistplaylist.dto.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for Spotify user profile data from /me endpoint.
 */
@Getter
@Setter
public class SpotifyUserProfileDTO {
    private String id;
    @JsonProperty("display_name")
    private String displayName;
    private String email;
    private String country;
    private String product; // premium, free, etc.
}
