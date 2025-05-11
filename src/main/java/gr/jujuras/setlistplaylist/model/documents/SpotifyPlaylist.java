package gr.jujuras.setlistplaylist.model.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("spotify-playlists")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class SpotifyPlaylist {
    @Id
    private String id;
    private String playlistName;
    private String spotifyPlaylistId;     // returned by Spotify
    private String spotifyUserId;         // from /me endpoint
    private String artistName;            // setlist source
    private List<String> trackUris;
    private LocalDateTime createdAt;
}
