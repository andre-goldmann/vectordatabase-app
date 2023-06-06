package de.goldman.csv.mataoln;

import com.opencsv.CSVParser;
import de.goldman.csv.mataoln.domain.model.SiteEntity;
import de.goldman.csv.mataoln.domain.model.SiteEntityRepository;
import de.goldman.csv.mataoln.infrastructure.csv.LineParser;
import de.goldman.csv.mataoln.infrastructure.milvus.LoadGoogleNewsModelWithReflection;
import de.goldman.csv.mataoln.infrastructure.milvus.MilvusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static de.goldman.csv.mataoln.infrastructure.milvus.MilvusService.COLLECTION_NAME;

@Slf4j
@SpringBootApplication
public class CsvMataolnApplication implements CommandLineRunner {

	@Autowired
	private LineParser lineParser;

	@Autowired
	private SiteEntityRepository siteEntityRepository;

	@Autowired
	MilvusService milvusService;



	public static void main(String[] args) {
		SpringApplication.run(CsvMataolnApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		this.milvusService.dropCollection(COLLECTION_NAME);

	}


}
