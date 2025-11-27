package gr.jujuras.setlistplaylist.services;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.jujuras.setlistplaylist.core.exceptions.ArtistNotFoundException;
import gr.jujuras.setlistplaylist.core.exceptions.SetNotFoundException;
import gr.jujuras.setlistplaylist.dto.SetDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * Service for retrieving and processing setlist data from Setlist.fm API.
 * This service integrates with MusicBrainz to resolve artist names to MBIDs,
 * then fetches setlist information from Setlist.fm.
 *
 * @author Setlist-Playlist Team
 * @version 1.0
 */
@Service
public class SetlistService {

    private static final Logger logger = LoggerFactory.getLogger(SetlistService.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    @Value("${setlistfm.api-key}")
    private String  apiKey;
    @Value("${setlistfm.base-url}")
    private String baseUrl;
    @Value("${musicbrainz.base-url}")
    private String musicbrainzBaseUrl;



    /**
     * Constructs a new SetlistService with required dependencies.
     *
     * @param restClient   the REST client for making HTTP requests
     * @param objectMapper the Jackson ObjectMapper for JSON parsing
     */
    public SetlistService(RestClient restClient,
                          ObjectMapper objectMapper
                          ) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;

    }

    /**
     * Retrieves the first valid setlist for a given artist.
     * A valid setlist is one that contains at least one set with more than 5 songs.
     *
     * @param artistName the name of the artist to search for
     * @return a SetDTO containing the artist name and list of songs
     * @throws ArtistNotFoundException if the artist cannot be found in MusicBrainz
     * @throws SetNotFoundException if no valid setlist is found for the artist
     */
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

    /**
     * Fetches setlist data for a given artist from Setlist.fm API.
     * First resolves the artist name to a MusicBrainz ID (MBID), then retrieves
     * the setlists for that artist.
     *
     * @param artistName the name of the artist to search for
     * @param page the page number for pagination (1-indexed)
     * @return JSON string containing setlist data from Setlist.fm API
     * @throws ArtistNotFoundException if the artist cannot be found in MusicBrainz
     */
    public String getSetlistsByArtist(String artistName, int page)  {
        String mbid = getMbidFromArtistName(artistName)
                .orElseThrow(() -> new ArtistNotFoundException("Artist was not found " + artistName));
        return fetchSetlists(mbid, page);
    }

    /**
     * URL-encodes the artist name for use in API requests.
     *
     * @param artistName the artist name to encode
     * @return the URL-encoded artist name
     */
    private String artistNameEncoder(String artistName) {
        return UriUtils.encode(artistName, StandardCharsets.UTF_8);
    }

    /**
     * Retrieves the MusicBrainz ID (MBID) for a given artist name.
     * Queries the MusicBrainz API and returns the MBID of the first matching artist.
     *
     * @param artistName the name of the artist to search for
     * @return an Optional containing the MBID if found, or empty if not found or on error
     */
    private Optional<String> getMbidFromArtistName(String artistName) {
        var encodedArtist = artistNameEncoder(artistName);
        String url = musicbrainzBaseUrl + encodedArtist + "&fmt=json";

        try {
            logger.debug("Fetching MBID for artist: {}", artistName);
            String response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode artists = root.path("artists");

            if (artists.isArray() && !artists.isEmpty()) {
                String mbid = artists.get(0).get("id").asText();
                logger.info("Successfully fetched MBID for artist '{}': {}", artistName, mbid);
                return Optional.ofNullable(mbid);
            }

            logger.warn("No artists found in MusicBrainz for: {}", artistName);
        } catch (Exception ex) {
            logger.error("Error fetching MBID from MusicBrainz for artist '{}': {}", artistName, ex.getMessage(), ex);
        }

        return Optional.empty();
    }

    /**
     * Fetches setlist data from Setlist.fm API using the artist's MBID.
     *
     * @param mbid the MusicBrainz ID of the artist
     * @param page the page number for pagination
     * @return JSON string containing setlist data
     * @throws SetNotFoundException if no setlist is found or API request fails
     */
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

    /**
     * Parses the JSON response from Setlist.fm API and extracts the setlist array.
     *
     * @param json the raw JSON string from the API response
     * @return a JsonNode representing the array of setlists
     * @throws SetNotFoundException if the JSON structure is invalid
     * @throws RuntimeException if JSON parsing fails
     */
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

    /**
     * Extracts the first valid set from a setlist.
     * A valid set must contain more than 5 songs.
     *
     * @param setlist the JsonNode representing a single setlist
     * @return an Optional containing a list of song names if a valid set is found, or empty otherwise
     * @throws SetNotFoundException if the setlist structure is invalid
     */
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






