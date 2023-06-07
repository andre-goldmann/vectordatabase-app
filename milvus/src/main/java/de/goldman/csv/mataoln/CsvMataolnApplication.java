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
		if(!this.milvusService.hasCollection()){
			this.milvusService.setup(COLLECTION_NAME);
			this.milvusService.createIndex();
			//throw new IllegalArgumentException("Collection " + COLLECTION_NAME + " does not exist!");
		}

		this.milvusService.showCollections();

		long count = this.siteEntityRepository.count();
		if(count == 0) {
			// TODO nicht in DB speichern sondern direkt in Milvus
			List<String> files = List.of("All-Live-Shopify-Sites.csv", "All-Live-WooCommerce-Sites.csv");
			for (String file : files) {
				try (FileInputStream inputStream = new FileInputStream("data/" + file); Scanner sc = new Scanner(inputStream, StandardCharsets.UTF_8)) {
					//try (FileInputStream inputStream = new FileInputStream("data/All-Live-Shopify-Sites.csv"); Scanner sc = new Scanner(inputStream, StandardCharsets.UTF_8)) {
					AtomicInteger counter = new AtomicInteger();
					final Set<String> dateTimeParseExceptions = new HashSet<>();
					final Set<String> errors = new HashSet<>();
					final CSVParser csvParser = new CSVParser();
					List<SiteEntity> entityList = new ArrayList<>();
					while (sc.hasNextLine()) {
						final String line = sc.nextLine();
						if (lineParser.parsLine(entityList, counter, dateTimeParseExceptions, errors, csvParser, line)) continue;
					}
					// note that Scanner suppresses exceptions
					if (sc.ioException() != null) {
						throw sc.ioException();
					}
					// TODO handle errors
					log.warn(errors.size() + " lines could not be read");
					log.warn(dateTimeParseExceptions.size() + " dateTimeParseExceptions occured");
					if (!dateTimeParseExceptions.isEmpty()) {
						dateTimeParseExceptions.forEach(System.out::println);
					}
					System.out.println(counter + " lines read!");
				}
			}
		}else
		{
			log.info("Data allready stored!");
		}

	}


}
