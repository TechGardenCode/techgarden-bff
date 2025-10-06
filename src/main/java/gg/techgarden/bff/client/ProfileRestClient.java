package gg.techgarden.bff.client;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class ProfileRestClient {


    @Value("${bff.services.profile:http://localhost:8082}")
    private String profileServiceUrl;
    private final RestClient restClient;

    public void putProfile(String sub) {
        this.restClient
                .put()
                .uri(profileServiceUrl + "/profiles/{sub}", sub)
                .retrieve()
                .body(Void.class);
    }
}
