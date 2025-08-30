package gg.techgarden.bff.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BlogClient {
//    private final RestClient rest;
//    private final String baseUrl;
//
//    public BlogClient(@Value("${app.blog.base-url}") String baseUrl) {
//        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length()-1) : baseUrl;
//        this.rest = RestClient.builder().baseUrl(this.baseUrl).build();
//    }
//
//    public String getMetadata() {
//        return rest.get()
//                .uri("/posts/metadata")
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .body(String.class);
//    }
//
//    public String getPost(String id) {
//        return rest.get()
//                .uri("/posts/{id}", id)
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .body(String.class);
//    }
//
//    public String createPost(String bearer, String bodyJson) {
//        return rest.post()
//                .uri("/posts")
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearer)
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(bodyJson)
//                .retrieve()
//                .body(String.class);
//    }
}
