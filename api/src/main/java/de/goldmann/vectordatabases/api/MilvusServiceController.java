package de.goldmann.vectordatabases.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/milvus")
public class MilvusServiceController {
    @GetMapping
    public String helloWorld(){
        return "Hello World from milvus";
    }
    @GetMapping("/search")
    public String milvusSearch(
            final String searchedDomain){
        return "OK";
    }
}
