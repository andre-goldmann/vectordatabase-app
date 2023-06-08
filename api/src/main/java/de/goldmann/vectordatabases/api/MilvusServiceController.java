package de.goldmann.vectordatabases.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/milvus")
public class MilvusServiceController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${milvus.url}")
    private String milvusUrl;

    @GetMapping
    public String helloWorld(){
        return "Hello World from milvus";
    }
    //http://localhost:8080/sites/milvus/search?searchedDomain=tedt
    @GetMapping("/search")
    public String milvusSearch(
            final String searchedDomain){
        log.info("searchedDomain={}", searchedDomain);
        final HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        final HttpEntity<?> entity = new HttpEntity<>(headers);
        final String urlTemplate = UriComponentsBuilder.fromHttpUrl(this.milvusUrl)
        .queryParam("searchedDomain", "{searchedDomain}")
        .encode()
        .toUriString();
        final Map<String, String> params = new HashMap<>();
        params.put("searchedDomain", searchedDomain);
        final HttpEntity<String> response = this.restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                String.class,
                params
        );
//        HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
//        HttpEntity<?> entity = new HttpEntity<>(headers);
//
//        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
//                .queryParam("msisdn", "{msisdn}")
//                .queryParam("email", "{email}")
//                .queryParam("clientVersion", "{clientVersion}")
//                .queryParam("clientType", "{clientType}")
//                .queryParam("issuerName", "{issuerName}")
//                .queryParam("applicationName", "{applicationName}")
//                .encode()
//                .toUriString();
//
//        Map<String, ?> params = new HashMap<>();
//        params.put("msisdn", msisdn);
//        params.put("email", email);
//        params.put("clientVersion", clientVersion);
//        params.put("clientType", clientType);
//        params.put("issuerName", issuerName);
//        params.put("applicationName", applicationName);
//
//        HttpEntity<String> response = restOperations.exchange(
//                urlTemplate,
//                HttpMethod.GET,
//                entity,
//                String.class,
//                params
//        );

        return response.getBody();
    }
}
