package de.goldman.csv.mataoln.infrastructure.csv;

import com.opencsv.CSVParser;
import de.goldman.csv.mataoln.domain.model.SiteEntity;
import de.goldman.csv.mataoln.domain.model.SiteEntityRepository;
import de.goldman.csv.mataoln.infrastructure.milvus.LoadGoogleNewsModelWithReflection;
import de.goldman.csv.mataoln.infrastructure.milvus.MilvusService;
import de.goldman.csv.mataoln.infrastructure.rest.FastApiConnectController;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static de.goldman.csv.mataoln.infrastructure.milvus.MilvusService.COLLECTION_NAME;

@Slf4j
@Service
public class LineParser {

    @Autowired
    SiteEntityRepository siteEntityRepository;

    @Autowired
    MilvusService milvusService;

    @Autowired
    FastApiConnectController fastApiConnectController;

    @Transactional
    public boolean parsLine(
            final List<SiteEntity> entityList,
            final AtomicInteger counter,
            final Set<String> dateTimeParseExceptions,
            final Set<String> errors,
            final CSVParser csvParser,
            final String line) {

        String[] parsed = null;
        try {
            parsed = csvParser.parseLine(line);
            if (parsed.length <= 1) {
                errors.add(line);
                return true;
            }

            counter.incrementAndGet();

            if (counter.get() % 20000 == 0 && parsed.length > 2) {
                System.out.println(counter + " lines read!");
                //System.out.println(parsed[0] + ", " + parsed[1] + ", " + parsed[2] + ", " + parsed[3] + ", " + parsed[4] + ", " + parsed[5]);
                //System.out.println("Split:" + split.length + ", parsed: " + parsed.length);
                //					System.out.println("Actual content:" + split[0] + ", " + split[1]);
                //					System.out.println("Rows:" + split.length);
                //System.out.println();
            }
            if (counter.get() > 1) {
                //System.out.println(line);
                final SiteEntity entity = new SiteEntity();
                entity.setLine(line);
                entity.setDomain(parsed[0]);
                if(!StringUtils.isEmpty(parsed[1])) {
                    String[] locations = parsed[1].split(";");
                    for(String location: locations) {
                        entity.addLocationOnSite(location);
                    }
                }
                entity.setTechSpendUsd(parsed[2]);
                entity.setSalesRevenueUsd(parsed[3]);
                if (!StringUtils.isEmpty(parsed[4])) {
                    entity.setSocial(Integer.parseInt(parsed[4]));
                }
                if (!StringUtils.isEmpty(parsed[5])) {
                    entity.setEmployees(Integer.parseInt(parsed[5]));
                }
                entity.setCompany(parsed[6].getBytes(StandardCharsets.UTF_8));
                entity.setVertical(parsed[7]);
//						if(!StringUtils.isEmpty(parsed[8]) && !"Outside Top 1m".equals(parsed[8])) {
//							entity.setTranco(Integer.parseInt(parsed[8]));
//						}
                entity.setTranco(parsed[8]);
//						if(!StringUtils.isEmpty(parsed[9]) && !"Outside Top 1m".equals(parsed[9])) {
//							entity.setPageRank(Integer.parseInt(parsed[9]));
//						}
                entity.setPageRank(parsed[9]);
//						if(!StringUtils.isEmpty(parsed[10]) && !"Outside Top 1m".equals(parsed[10])) {
//							entity.setMajestic(Integer.parseInt(parsed[10]));
//						}
                entity.setMajestic(parsed[10]);
//						if(!StringUtils.isEmpty(parsed[11]) && !"Outside Top 1m".equals(parsed[11])) {
//							entity.setUmbrella(Integer.parseInt(parsed[11]));
//						}
                entity.setMajestic(parsed[10]);
                if(!StringUtils.isEmpty(parsed[12])) {
                    String[] telepnones = parsed[12].split(";");
                    for(String telephone:telepnones) {
                        entity.addTelephones(telephone);
                    }
                }
                if(!StringUtils.isEmpty(parsed[13])) {
                    String[] emails = parsed[13].split(";");
                    for(String email:emails) {
                        entity.addEmail(email);
                    }
                }
                entity.setTwitter(parsed[14]);
                entity.setFacebook(parsed[15]);
                entity.setLinkedIn(parsed[16]);
                entity.setGoogle(parsed[17]);
                entity.setPinterest(parsed[18]);
                entity.setGitHub(parsed[19]);
                entity.setInstagram(parsed[20]);
                entity.setVk(parsed[21]);
                entity.setVimeo(parsed[22]);
                entity.setYoutube(parsed[23]);
                entity.setTikTok(parsed[24]);
                if(!StringUtils.isEmpty(parsed[25])) {
                    String[] peoples = parsed[25].split(";");
                    for(String people: peoples) {
                        entity.addPeople(people);
                    }
                }
                entity.setCity(parsed[26]);
                entity.setState(parsed[27]);
                entity.setZip(parsed[28]);
                entity.setCountry(parsed[29]);
                entity.setFirstDetected(LocalDate.parse(parsed[30]));
                if(!StringUtils.isEmpty(parsed[31])) {
                    entity.setLastFound(LocalDate.parse(parsed[31]));
                }
                if(!StringUtils.isEmpty(parsed[32])) {
                    entity.setFirstIndexed(LocalDate.parse(parsed[32]));
                }
                if(!StringUtils.isEmpty(parsed[33])) {
                    entity.setLastIndexed(LocalDate.parse(parsed[33]));
                }
                entity.setExclusion(parsed[34]);
                entity.setCompliance(parsed[35]);
                //System.out.println(entity);

                entityList.add(entity);

                if(entityList.size() == 1000){
                    final List<String> domain_array = new ArrayList<>();
                    final List<Integer> employes_count_array = new ArrayList<>();
                    final List<Integer> social_intro_array = new ArrayList<>();
                    final List<List<Float>> vectors = new ArrayList<>();
                    entityList
                            .forEach(e -> {

                                vectors.add(fastApiConnectController.calculateSenteceVectors(e.getLine()));
                                domain_array.add(e.getDomain());
                                if(e.getEmployees() == null){
                                    employes_count_array.add(0);
                                }else {
                                    employes_count_array.add(e.getEmployees());
                                }
                                if(e.getSocial() == null){
                                    social_intro_array.add(0);
                                }else {
                                    social_intro_array.add(e.getSocial());
                                }
                            });
//                    log.info("########### VectorsTotal: " + vectors.size());
//                    vectors.forEach(e ->{
//                        log.info("########### Vectors-Single: " + e.size());
//                    });
                    this.milvusService.insertData(COLLECTION_NAME, domain_array, employes_count_array, social_intro_array, vectors);

                    //this.siteEntityRepository.saveAll(entityList);
                    log.info("Data Inserted!");
                    entityList.clear();
                }
            }
        }
        catch (DateTimeParseException e){
            if(parsed != null) {
                dateTimeParseExceptions.add("length=" + parsed.length + ": " + line);
            }else {
                dateTimeParseExceptions.add(line);
            }
        }
        catch (IOException e){
            log.error("", e);
            errors.add(line);
        }
        return false;
    }
}
