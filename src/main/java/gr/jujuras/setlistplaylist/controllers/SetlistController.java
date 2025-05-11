package gr.jujuras.setlistplaylist.controllers;

import gr.jujuras.setlistplaylist.dto.SetDTO;
import gr.jujuras.setlistplaylist.services.SetlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/setlists")
@RequiredArgsConstructor
public class SetlistController {

    private final SetlistService setlistService;

    @GetMapping("/{artistName}")
    public ResponseEntity<String> getSetlists(@PathVariable  String artistName,
                                              @RequestParam(defaultValue = "1") int page) {
       String response =  setlistService.getSetlistsByArtist(artistName, page);
        System.out.println("fetched data for " + artistName);


       return ResponseEntity.ok(response);

    }

    @GetMapping("/first-set/{artistName}")
    public ResponseEntity<SetDTO> getFirstSetByArtist(@PathVariable String artistName)  {
        System.out.println("Creating setist " + artistName);
        return ResponseEntity.ok(setlistService.getFirstValidSetByArtist(artistName));
    }


    @GetMapping("/test")
    public String test() {
        System.out.println("✅ Controller is working malaka");
        System.out.println(LocalDateTime.now());
        return "Controller is working ✅";
    }

}
