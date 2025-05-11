package gr.jujuras.setlistplaylist.services;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.jujuras.setlistplaylist.core.exceptions.ArtistNotFoundException;
import gr.jujuras.setlistplaylist.core.exceptions.SetNotFoundException;
import gr.jujuras.setlistplaylist.dto.SetDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class SetlistService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    @Value("${setlistfm.api-key}")
    private String  apiKey;
    @Value("${setlistfm.base-url}")
    private String baseUrl;



    public SetlistService(RestClient restClient,
                          ObjectMapper objectMapper
                          ) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;

    }


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
                .orElseThrow(() -> new ArtistNotFoundException("Artist was not found " + artistName));
        return fetchSetlists(mbid, page);
    }

    private String artistNameEncoder(String artistName) {
        return UriUtils.encode(artistName, StandardCharsets.UTF_8);
    }


    private Optional<String> getMbidFromArtistName(String artistName) {
        var encodedArtist = artistNameEncoder(artistName);
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
            throw new SetNotFoundException("No setlist found");
        }

    }


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






