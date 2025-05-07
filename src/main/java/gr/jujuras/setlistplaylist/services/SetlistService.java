package gr.jujuras.setlistplaylist.services;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.jujuras.setlistplaylist.core.exceptions.ArtistNotFoundException;
import gr.jujuras.setlistplaylist.core.exceptions.SetNotFoundException;
import gr.jujuras.setlistplaylist.dto.SetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class SetlistService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String  apiKey;
    private final String baseUrl;


    @Autowired
    public SetlistService(RestClient restClient,
                          ObjectMapper objectMapper,
                          @Value("${setlistfm.api-key}") String apiKey,
                          @Value("${setlistfm.base-url}") String baseUrl) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

//    public SetDTO getFirstValidSetByArtist(String artistName) throws SetNotFoundException, ArtistNotFoundException {
//        String json = getSetlistsByArtist(artistName, 1);
//        JsonNode setlists = parseSetlistArray(json);
//
//        for (JsonNode setlist : setlists) {
//            List<String> validSet = extractFirstValidSet(setlist);
//            if (validSet != null) {
//                return new SetDTO(artistName, validSet);
//            }
//        }
//
//        throw new SetNotFoundException("No valid set (more than 5 songs) found for artist: " + artistName);
//    }

    public SetDTO getFirstValidSetByArtist(String artistName) {
        String json = getSetlistsByArtist(artistName, 1);
        JsonNode setlists = parseSetlistArray(json);

        for (JsonNode setlist : setlists) {
            Optional<List<String>> validSet = extractFirstValidSet(setlist);
            if (validSet.isPresent()) {
                return new SetDTO(artistName, validSet.get());
            }
        }

        throw new SetNotFoundException(artistName);
    }

    public String getSetlistsByArtist(String artistName, int page)  {
        String mbid = getMbidFromArtistName(artistName)
                .orElseThrow(() -> new ArtistNotFoundException("Artist was not found" + artistName));
        return fetchSetlists(mbid, page);
    }


    private Optional<String> getMbidFromArtistName(String artistName) {
        String encodedArtist = UriUtils.encode(artistName, StandardCharsets.UTF_8);
        String url = "http://musicbrainz.org/ws/2/artist/?query=" + encodedArtist + "&fmt=json";

        try {
            String response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode artists = root.path("artists");

            if (artists.isArray() && !artists.isEmpty()) {
                System.out.println("fetching the mbid");
                return Optional.ofNullable(artists.get(0).get("id").asText());
            }

        } catch (Exception ex) {
            ex.getMessage();
        }

        return Optional.empty();
    }

    private String fetchSetlists(String mbid, int page) {
        String url = baseUrl + "artist/" + mbid + "/setlists?p=" + page;

        try {
            return restClient.get()
                    .uri(url)
                    .headers(headers -> {
                        headers.set("x-api-key", apiKey);
                        headers.set("Accept", "application/json");
                        headers.set("User-Agent", "my-java-client");
                    })
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            throw new SetNotFoundException("");
        }

    }
//    public SetDTO getFirstValidSetByArtist(String artistName) throws ArtistNotFoundException {
//        String setlistJson = getSetlistsByArtist(artistName, 1);
//
//        try {
//            JsonNode root = objectMapper.readTree(setlistJson);
//            JsonNode setlists = root.path("setlist");
//
//            if (!setlists.isArray() || setlists.isEmpty()) {
//                throw new SetNotFoundException("No setlists found for artist: " + artistName);
//            }
//
//            for (JsonNode setlist : setlists) {
//                JsonNode sets = setlist.path("sets").path("set");
//
//                if (sets.isArray()) {
//                    for (JsonNode set : sets) {
//                        JsonNode songs = set.path("song");
//
//                        if (songs.isArray() && songs.size() > 5) {
//                            List<String> songNames = new ArrayList<>();
//                            for (JsonNode song : songs) {
//                                songNames.add(song.path("name").asText());
//                            }
//                            return new SetDTO(artistName, songNames);
//                        }
//                    }
//                }
//            }
//
//            throw new SetNotFoundException("No valid set (more than 5 songs) found for artist: " + artistName);
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to parse setlist data: " + e.getMessage(), e);
//        }

//    }

    private JsonNode parseSetlistArray(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode setlists = root.path("setlist");

            if (!setlists.isArray()) {
                throw new SetNotFoundException("Invalid setlist structure in API response.");
            }

            return setlists;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse setlist JSON", e);
        }
    }


    private Optional<List<String>> extractFirstValidSet(JsonNode setlist)  {
        JsonNode sets = setlist.path("sets").path("set");
        if (!sets.isArray()) throw new SetNotFoundException("No valid setlist");

        return StreamSupport.stream(sets.spliterator(), false)
                .map(set -> set.path("song"))
                .filter(JsonNode::isArray)
                .filter(songs -> songs.size() > 5)
                .findFirst()
                .map(songs -> StreamSupport.stream(songs.spliterator(), false)
                        .map(song -> song.path("name").asText())
                        .toList());
    }

}






