package gr.jujuras.setlistplaylist.controllers;

import gr.jujuras.setlistplaylist.services.SetlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/setlists")
@RequiredArgsConstructor
public class SetlistController {

    private final SetlistService setlistService;

    @GetMapping("/{artist}")
    public ResponseEntity<String> getSetlists(@PathVariable  String artist) {
       String response =  setlistService.fetchSetlists(artist);

       return ResponseEntity.ok(response);

    }

}
