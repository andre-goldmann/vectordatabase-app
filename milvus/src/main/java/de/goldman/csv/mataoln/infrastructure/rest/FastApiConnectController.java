package de.goldman.csv.mataoln.infrastructure.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class FastApiConnectController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${sentenceapi.url}")
    private String sentenceapiUrl;

    public List<Float> calculateSenteceVectors(final String sentence){
        final String fooResourceUrl
                = this.sentenceapiUrl + "/sentences/translator/all-MiniLM-L6-v2/text={text}";

        final DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
        final URI uri = factory.uriString(fooResourceUrl).build(sentence);

        final ResponseEntity<Float[]> response
                = this.restTemplate.getForEntity(uri, Float[].class);
        return Arrays.asList(Objects.requireNonNull(response.getBody()));
    }
}
