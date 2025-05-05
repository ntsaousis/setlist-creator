package gr.jujuras.setlistplaylist.controllers;

import gr.jujuras.setlistplaylist.core.exceptions.ArtistNotFoundException;
import gr.jujuras.setlistplaylist.dto.SetDTO;
import gr.jujuras.setlistplaylist.services.SetlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/setlists")
@RequiredArgsConstructor
public class SetlistController {

    private final SetlistService setlistService;

    @GetMapping("/{artistName}")
    public ResponseEntity<String> getSetlists(@PathVariable  String artistName,
                                              @RequestParam(defaultValue = "1") int page) throws ArtistNotFoundException {
       String response =  setlistService.getSetlistsByArtist(artistName, page);
        System.out.println("fetched data for " + artistName);


       return ResponseEntity.ok(response);

    }

    @GetMapping("/first-set/{name}")
    public ResponseEntity<SetDTO> getFirstSetByArtist(@PathVariable String name) throws ArtistNotFoundException {
        return ResponseEntity.ok(setlistService.getFirstValidSetByArtist(name));
    }


    @GetMapping("/test")
    public String test() {
        System.out.println("✅ Controller is working malaka");
        System.out.println(LocalDateTime.now());
        return "Controller is working";
    }

}
