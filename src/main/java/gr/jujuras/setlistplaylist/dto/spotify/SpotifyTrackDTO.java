package gr.jujuras.setlistplaylist.dto.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO for Spotify track data.
 */
@Getter
@Setter
public class SpotifyTrackDTO {
    private String id;
    private String name;
    private String uri;
    private List<ArtistDTO> artists;
    private AlbumDTO album;
    @JsonProperty("duration_ms")
    private long durationMs;
    private int popularity;

    @Getter
    @Setter
    public static class ArtistDTO {
        private String id;
        private String name;
        private String uri;
    }

    @Getter
    @Setter
    public static class AlbumDTO {
        private String id;
        private String name;
        @JsonProperty("release_date")
        private String releaseDate;
    }
}
