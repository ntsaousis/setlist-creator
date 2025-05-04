package gr.jujuras.setlistplaylist.controllers;

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

    @GetMapping("/{mbid}")
    public ResponseEntity<String> getSetlists(@PathVariable  String mbid,
                                              @RequestParam(defaultValue = "1") int page) {
       String response =  setlistService.fetchSetlists(mbid, page);
        System.out.println("fetched data for " + mbid);

       return ResponseEntity.ok(response);

    }

    @GetMapping("/test")
    public String test() {
        System.out.println("✅ Controller is working malaka");
        System.out.println(LocalDateTime.now());
        return "Controller is working";
    }

}
