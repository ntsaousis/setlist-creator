package gr.jujuras.setlistplaylist.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OAuth2Service {

    @Value("${spotify.client-id}")
    private String client_id;

    @Value("${spotify.redirect-uri}")
    private String redirectUri;

    private final String scope = "";
}
