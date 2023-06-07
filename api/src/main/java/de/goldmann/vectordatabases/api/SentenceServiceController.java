package de.goldmann.vectordatabases.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sentences")
public class SentenceServiceController {

    @GetMapping
    public String helloWorld(){
        return "Hello World from sentences";
    }

    //"http://94.16.104.209:9081/translator/all-MiniLM-L6-v2/?text=
    @GetMapping("/translator/{modelname}/{text}")
    public String translator(@PathVariable final String modelname,
                             @PathVariable final String text){
        return "Hello World from sentences";
    }
}
