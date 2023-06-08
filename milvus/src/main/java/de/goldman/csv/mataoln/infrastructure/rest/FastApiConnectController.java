package de.goldman.csv.mataoln.infrastructure.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Service
@Slf4j
public class FastApiConnectController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${sentenceapi.url}")
    private String sentenceapiUrl;

    public List<Float> calculateSenteceVectors(final String sentence){
//        final String fooResourceUrl
//                = this.sentenceapiUrl + "/sentences/translator/all-MiniLM-L6-v2/text={text}";
//
//        final DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
//        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
//        final URI uri = factory.uriString(fooResourceUrl).build(sentence);
//
//        final ResponseEntity<Float[]> response
//                = this.restTemplate.getForEntity(uri, Float[].class);
//        return Arrays.asList(Objects.requireNonNull(response.getBody()));

        //log.info("text={}");
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
        params.put("modelname", "all-MiniLM-L6-v2");
        params.put("text", sentence);
        final HttpEntity<Float[]> response = this.restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                Float[].class,
                params
        );
        return Arrays.asList(Objects.requireNonNull(response.getBody()));
    }
}
