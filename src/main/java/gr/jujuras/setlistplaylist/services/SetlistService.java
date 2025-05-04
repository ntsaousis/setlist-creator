package gr.jujuras.setlistplaylist.services;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class SetlistService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${setlistfm.api-key}")
    private String  apiKey ;

    @Value("${setlistfm.base-url}")
    private  String baseUrl;

    public String fetchSetlists(String artistName) {
        String url = baseUrl + "search/setlists?artistName=" + artistName;

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }


}
