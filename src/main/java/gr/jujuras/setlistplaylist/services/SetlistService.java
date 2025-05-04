package gr.jujuras.setlistplaylist.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class SetlistService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

//    @Value("${setlistfm.api-key}")
    private String  apiKey  = "s9OCbtCM2YvcineTpHgobX9rNhtaoQtpGvRn";

//    @Value("${setlistfm.base-url}")
    private  String baseUrl = "https://api.setlist.fm/rest/1.0/";




    public String fetchSetlists(String mbid, int page) {
        String url = baseUrl + "artist/" + mbid + "/setlists?p=" + page;


        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey );
        headers.set("Accept", "application/json");
        headers.set("User-Agent","my-java-client");



        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return  response.getBody();


    }




}
