package de.goldmann.vectordatabases.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/sentences")
public class SentenceServiceController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${sentenceapi.url}")
    private String sentenceapiUrl;

    @GetMapping
    public String helloWorld(){
        return "Hello World from sentences";
    }

    @GetMapping("/translator/{modelname}/{text}")
    public String translator(@PathVariable final String modelname,
                             @PathVariable final String text){
        log.info("modelname={}, text={}", modelname, text);
        final HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJoZWxsbyI6IndvcmxkIn0.bqxXg9VwcbXKoiWtp-osd0WKPX307RjcN7EuXbdq-CE");
        final HttpEntity<?> entity = new HttpEntity<>(headers);
        final String urlTemplate = UriComponentsBuilder.fromHttpUrl(this.sentenceapiUrl + "/translator/")
                .queryParam("modelname", "{modelname}")
                .queryParam("text", "{text}")
                .encode()
                .toUriString();
        final Map<String, String> params = new HashMap<>();
        params.put("modelname", modelname);
        params.put("text", text);
        final HttpEntity<String> response = this.restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                String.class,
                params
        );
        return response.getBody();
    }

    @GetMapping("/splitter/{text}")
    public String splitText( @PathVariable final String text){
        log.info("text={}", text);
        final HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJoZWxsbyI6IndvcmxkIn0.bqxXg9VwcbXKoiWtp-osd0WKPX307RjcN7EuXbdq-CE");
        final HttpEntity<?> entity = new HttpEntity<>(headers);
        final String urlTemplate = UriComponentsBuilder.fromHttpUrl(this.sentenceapiUrl + "/splitter/")
                .queryParam("text", "{text}")
                .encode()
                .toUriString();
        final Map<String, String> params = new HashMap<>();
        params.put("text", text);
        final HttpEntity<String> response = this.restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                String.class,
                params
        );
        return response.getBody();
    }
}
