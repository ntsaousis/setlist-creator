package gr.jujuras.setlistplaylist.dto.spotify;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO for Spotify track search response.
 */
@Getter
@Setter
public class SpotifyTrackSearchResponseDTO {
    private TracksWrapper tracks;

    @Getter
    @Setter
    public static class TracksWrapper {
        private List<SpotifyTrackDTO> items;
        private int total;
        private int limit;
        private int offset;
    }
}
