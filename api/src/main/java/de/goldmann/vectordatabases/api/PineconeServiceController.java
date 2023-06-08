package de.goldmann.vectordatabases.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.ui.Model;

@Slf4j
@RestController
@RequestMapping("/pinecone")
public class PineconeServiceController {

    public static final String SECRET = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJoZWxsbyI6IndvcmxkIn0.bqxXg9VwcbXKoiWtp-osd0WKPX307RjcN7EuXbdq-CE";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${pinecone.url}")
    private String pineconeUrl;

    @GetMapping
    public String helloWorld(){
        return "Hello World from pinecone";
    }

    @PostMapping("/uploadfiles")
    public String uploadfiles(
            @RequestParam("files") MultipartFile[] files,
            @ModelAttribute PineconeUploadForm formData, final Model model){
        log.info("files={}, fromData={}", files, formData);
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        try {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    map.add("files", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
                }
            }
        } catch (IOException ioe){
          log.error("", ioe);
        }
        final HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", SECRET);
        HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
        final String urlTemplate = UriComponentsBuilder.fromHttpUrl(this.pineconeUrl + "/pinecone/uploadfiles/")
                .queryParam("apiKey", "{apiKey}")
                .queryParam("modelName", "{modelName}")
                .queryParam("indexName", "{indexName}")
                .queryParam("environment", "{environment}")
                .queryParam("metric", "{metric}")
                .encode()
                .toUriString();
        final Map<String, String> params = new HashMap<>();
        params.put("apiKey", formData.getApiKey());
        params.put("modelName", formData.getModelName());
        params.put("indexName", formData.getIndexName());
        params.put("environment", formData.getEnvironment());
        params.put("metric", formData.getMetric());
        final HttpEntity<String> response = this.restTemplate.exchange(
                urlTemplate,
                HttpMethod.POST,
                entity,
                String.class,
                params
        );
        return response.getBody();

    }

    @PostMapping("/uploadfiles/url")
    public String uploadfilesByUrl(
            @ModelAttribute PineconeUploadForm formData, final  Model model){

        log.info("formData={}", formData);

        final HttpHeaders headers = getHttpHeaders();
        final HttpEntity<?> entity = new HttpEntity<>(headers);
        final String urlTemplate = UriComponentsBuilder.fromHttpUrl(this.pineconeUrl + "/pinecone/uploadfiles/url/")
                .queryParam("apiKey", "{apiKey}")
                .queryParam("modelName", "{modelName}")
                .queryParam("indexName", "{indexName}")
                .queryParam("environment", "{environment}")
                .queryParam("metric", "{metric}")
                .queryParam("url", "{url}")
                .encode()
                .toUriString();
        final Map<String, String> params = new HashMap<>();
        params.put("apiKey", formData.getApiKey());
        params.put("modelName", formData.getModelName());
        params.put("indexName", formData.getIndexName());
        params.put("environment", formData.getEnvironment());
        params.put("metric", formData.getMetric());
        params.put("url", formData.getUrl());
        final HttpEntity<String> response = this.restTemplate.exchange(
                urlTemplate,
                HttpMethod.POST,
                entity,
                String.class,
                params
        );
        return response.getBody();
    }

    @GetMapping("/fileinfo/{filename}")
    public String fileInfo(@PathVariable String filename){

        log.info("filename={}", filename);
        final HttpHeaders headers = getHttpHeaders();
        final HttpEntity<?> entity = new HttpEntity<>(headers);
        final String urlTemplate = UriComponentsBuilder.fromHttpUrl(this.pineconeUrl + "/pinecone/fileinfo/")
                .queryParam("filename", "{filename}")
                .encode()
                .toUriString();
        final Map<String, String> params = new HashMap<>();
        params.put("filename", filename);
        final HttpEntity<String> response = this.restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                String.class,
                params
        );
        return response.getBody();
    }

    @GetMapping("/indexinfo/{apiKey}/{indexName}/{environment}")
    public String pineconeInfo(@PathVariable String apiKey,
                               @PathVariable String indexName,
                               @PathVariable String environment){
        log.info("apikey={}, indexName={}, environment={}", apiKey, indexName, environment);
        final HttpHeaders headers = getHttpHeaders();
        final HttpEntity<?> entity = new HttpEntity<>(headers);
        final String urlTemplate = UriComponentsBuilder.fromHttpUrl(this.pineconeUrl + "/pinecone/indexinfo/")
                .queryParam("apiKey", "{apiKey}")
                .queryParam("indexName", "{indexName}")
                .queryParam("environment", "{environment}")
                .encode()
                .toUriString();
        final Map<String, String> params = new HashMap<>();
        params.put("apiKey", apiKey);
        params.put("indexName", indexName);
        params.put("environment", environment);
        final HttpEntity<String> response = this.restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                String.class,
                params
        );
        return response.getBody();
    }

    @GetMapping("/listmodels")
    public String[] listModels(){
        final HttpHeaders headers = getHttpHeaders();
        final HttpEntity<?> entity = new HttpEntity<>(headers);
        final HttpEntity<String[]> response = this.restTemplate.exchange(
                this.pineconeUrl + "/pinecone/listmodels",
                HttpMethod.GET,
                entity,
                String[].class
        );
        log.info("models={}",  Arrays.toString(response.getBody()));
        return response.getBody();
    }

    @GetMapping("/listindexes")
    public String[] listindexes(){
        final HttpHeaders headers = getHttpHeaders();
        final HttpEntity<?> entity = new HttpEntity<>(headers);
        final HttpEntity<String[]> response = this.restTemplate.exchange(
                this.pineconeUrl + "/pinecone/listindexes",
                HttpMethod.GET,
                entity,
                String[].class
        );
        log.info("indexes={}",  Arrays.toString(response.getBody()));
        return response.getBody();
    }

    @GetMapping("/searchbyquery/{apiKey}/{indexName}/{environment}/{modelname}/{query}")
    public String searchbyquery(@PathVariable String apiKey,
                                @PathVariable String indexName,
                                @PathVariable String environment,
                                @PathVariable String modelname,
                                @PathVariable String query){
        log.info("apikey={}, indexName={}, environment={}, modelname={}, query={}", apiKey, indexName, environment, modelname, query);
        final HttpHeaders headers = getHttpHeaders();
        final HttpEntity<?> entity = new HttpEntity<>(headers);
        final String urlTemplate = UriComponentsBuilder.fromHttpUrl(this.pineconeUrl + "/pinecone/searchbyquery/")
                .queryParam("apiKey", "{apiKey}")
                .queryParam("indexName", "{indexName}")
                .queryParam("environment", "{environment}")
                .queryParam("modelName", "{modelName}")
                .queryParam("query", "{query}")
                .encode()
                .toUriString();
        final Map<String, String> params = new HashMap<>();
        params.put("apiKey", apiKey);
        params.put("indexName", indexName);
        params.put("environment", environment);
        params.put("modelName", modelname);
        params.put("query", query);
        final HttpEntity<String> response = this.restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                String.class,
                params
        );
        return response.getBody();
    }

    private static HttpHeaders getHttpHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set("Authorization", SECRET);
        return headers;
    }

}
