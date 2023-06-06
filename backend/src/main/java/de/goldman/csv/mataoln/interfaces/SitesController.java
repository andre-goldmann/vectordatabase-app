package de.goldman.csv.mataoln.interfaces;

import de.goldman.csv.mataoln.domain.SitesService;
import de.goldman.csv.mataoln.domain.model.SiteEntity;
import de.goldman.csv.mataoln.infrastructure.milvus.MilvusService;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import static de.goldman.csv.mataoln.infrastructure.milvus.MilvusService.COLLECTION_NAME;

@CrossOrigin
@RestController
@RequestMapping("/sites")
@Slf4j
public class SitesController {


    @Autowired
    SitesService sitesService;

    @Autowired
    MilvusService milvusService;

    // URLS:
    // http://localhost:8080/sites?page=1
    // http://localhost:8080/sites?page=1&pageSize=20
    @GetMapping
    public Page<SiteEntity> loadSites(
            @RequestParam(required = false, defaultValue = "0") final Integer page,
            @RequestParam(required = false, defaultValue = "10") final Integer pageSize,
            @RequestParam(defaultValue = "DESC", required = false) final Sort.Direction sortOrder) {
        return this.sitesService
                .loadSites(PageRequest.of(page, pageSize, Sort.by(sortOrder, "domain")));
    }

    @GetMapping("milvus")
    public String milvusSetup(){

//        Integer dropStatus = this.milvusService.dropCollection(collectionName);
//        System.out.println("Dropped Collection with status: " + dropStatus);

        R<RpcStatus> collection = milvusService.loadCollection();
        if (collection.getStatus() == R.Status.Success.getCode()) {
            System.out.println(collection.getData());
            // NPE
            //System.out.println(collection.getMessage());
            milvusService.prepateData(COLLECTION_NAME);
            return String.valueOf(collection.getStatus());
        }else {

            milvusService.setup(COLLECTION_NAME);
            return "CREATED, status was: " + collection.getStatus() ;
        }
    }

    @GetMapping("milvus/search")
    public String milvusSearch(
            final String searchedDomain){
        //this.milvusService.searchWithVector(searchedDomain);
        this.milvusService.searchDomainByExpression(searchedDomain);
        return "OK";
    }


}
