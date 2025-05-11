package gr.jujuras.setlistplaylist.controllers;

import gr.jujuras.setlistplaylist.services.SpotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/spotify")
public class SpotifyController {
    private final SpotifyService spotifyService;

    @Autowired
    public SpotifyController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @PostMapping("/test-save")
    public ResponseEntity<String> saveTest() {
        spotifyService.saveSamplePlaylist();
        return new ResponseEntity<>("Setlist was created", HttpStatus.CREATED);
    }
}
