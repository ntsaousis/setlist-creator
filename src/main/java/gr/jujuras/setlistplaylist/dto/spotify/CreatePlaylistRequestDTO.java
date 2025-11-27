package gr.jujuras.setlistplaylist.dto.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for creating a Spotify playlist.
 */
@Getter
@Setter
@AllArgsConstructor
public class CreatePlaylistRequestDTO {
    private String name;
    private String description;
    @JsonProperty("public")
    private boolean isPublic;
}
