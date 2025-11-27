package gr.jujuras.setlistplaylist.dto.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for Spotify playlist creation response.
 */
@Getter
@Setter
public class PlaylistResponseDTO {
    private String id;
    private String name;
    private String description;
    @JsonProperty("public")
    private boolean isPublic;
    @JsonProperty("external_urls")
    private ExternalUrls externalUrls;

    @Getter
    @Setter
    public static class ExternalUrls {
        private String spotify;
    }
}
