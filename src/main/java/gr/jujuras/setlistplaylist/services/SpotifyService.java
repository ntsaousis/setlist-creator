package gr.jujuras.setlistplaylist.services;


import gr.jujuras.setlistplaylist.model.documents.SpotifyPlaylist;
import gr.jujuras.setlistplaylist.repositories.mongo.SpotifyPlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SpotifyService {


    private final SpotifyPlaylistRepository playlistRepository;

    public SpotifyService(SpotifyPlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }


    public void saveSamplePlaylist() {
        SpotifyPlaylist playlist = new SpotifyPlaylist();
        playlist.setPlaylistName("Metal Gods");
        playlist.setSpotifyPlaylistId("7xY123Abcd");
        playlist.setSpotifyUserId("tsaousis");
        playlist.setArtistName("Sakianakis Notis");
        playlist.setTrackUris(List.of("spotify:track:1", "spotify:track:2","A Touch of Evil"));
        playlist.setCreatedAt(LocalDateTime.now());

        playlistRepository.save(playlist);
    }
}
