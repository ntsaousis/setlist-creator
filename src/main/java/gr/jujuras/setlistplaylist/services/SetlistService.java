package gr.jujuras.setlistplaylist.services;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.jujuras.setlistplaylist.core.exceptions.ArtistNotFoundException;
import gr.jujuras.setlistplaylist.core.exceptions.SetNotFoundException;
import gr.jujuras.setlistplaylist.dto.SetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public String getSetlistsByArtist(String artistName, int page) throws ArtistNotFoundException {
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
            throw new RuntimeException("Failed to fetch stelists");
        }

    }

    public SetDTO getFirstValidSetByArtist(String artistName) throws ArtistNotFoundException {
        String setlistJson = getSetlistsByArtist(artistName, 1);

        try {
            JsonNode root = objectMapper.readTree(setlistJson);
            JsonNode setlists = root.path("setlist");

            if (!setlists.isArray() || setlists.isEmpty()) {
                throw new SetNotFoundException("No setlists found for artist: " + artistName);
            }

            for (JsonNode setlist : setlists) {
                JsonNode sets = setlist.path("sets").path("set");

                if (sets.isArray()) {
                    for (JsonNode set : sets) {
                        JsonNode songs = set.path("song");

                        if (songs.isArray() && songs.size() > 5) {
                            List<String> songNames = new ArrayList<>();
                            for (JsonNode song : songs) {
                                songNames.add(song.path("name").asText());
                            }
                            return new SetDTO(artistName, songNames);
                        }
                    }
                }
            }

            throw new SetNotFoundException("No valid set (more than 5 songs) found for artist: " + artistName);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse setlist data: " + e.getMessage(), e);
        }
    }




}






