package de.goldmann.vectordatabases.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pinecone")
public class PineconeServiceController {
    @GetMapping
    public String helloWorld(){
        return "Hello World from pinecone";
    }

    @PostMapping("/uploadfiles")
    public String uploadfiles(){
        return "OK";
    }

    @PostMapping("/uploadfiles/url")
    public String uploadfilesByUrl(){
        return "OK";
    }

    @GetMapping("/fileinfo/{filename}")
    public String fileInfo(String fileName){
        return "OK";
    }

    @GetMapping("/indexinfo/{apiKey}/{indexName}/{environment}")
    public String pineconeInfo(String fileName){
        return "OK";
    }

    @GetMapping("/listmodels")
    public String listModels(){
        return "OK";
    }

    @GetMapping("/listindexes")
    public String listindexes(){
        return "OK";
    }

    @GetMapping("/fetchbyid")
    public String fetchbyid(){
        return "OK";
    }

    @GetMapping("/searchbyquery")
    public String searchbyquery(){
        return "OK";
    }
}
