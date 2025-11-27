package gr.jujuras.setlistplaylist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for playlist creation request from the frontend.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlaylistRequestDTO {
    private String artistName;
    private String playlistName;
}
