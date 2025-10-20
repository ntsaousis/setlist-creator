package gr.jujuras.setlistplaylist.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2Service {

    @Value("${spotify.client-id}")
    private String client_id;

    @Value("${spotify.redirect-uri}")
    private String redirectUri;

    private static final String scope = "user-read-private playlist-modify-public playlist-modify-private\n";
}
