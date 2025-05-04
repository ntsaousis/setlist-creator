package gr.jujuras.setlistplaylist.services;


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

    private final RestTemplate restTemplate = new RestTemplate();

//    @Value("${setlistfm.api-key}")
//    private String  apiKey ;
//
//    @Value("${setlistfm.base-url}")
//    private  String baseUrl;
//
//    @PostConstruct
//    public void testApiKeyInjection() {
//        System.out.println("✅ Injected API Key: " + apiKey);
//    }


    public String fetchSetlists(String mbid) {
//        String mbid = "b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d"; // The beatles
        String url = "https://api.setlist.fm/rest/1.0/" + "artist/" + mbid;


        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", "s9OCbtCM2YvcineTpHgobX9rNhtaoQtpGvRn" );
        headers.set("Accept", "application/json");
        headers.set("User-Agent","my-java-client");



        HttpEntity<String> entity = new HttpEntity<>(headers);

//
//        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            System.out.println("❌ HTTP Error: " + e.getStatusCode());
            System.out.println("❌ Response Body: " + e.getResponseBodyAsString());
            throw e;
        }


    }


}
