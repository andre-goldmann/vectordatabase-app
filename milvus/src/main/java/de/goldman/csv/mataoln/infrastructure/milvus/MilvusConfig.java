package de.goldman.csv.mataoln.infrastructure.milvus;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MilvusConfig {

    @Value("${milvus.host}")
    private String milvusHost;

    @Bean
    MilvusServiceClient milvusClient(){
        return new MilvusServiceClient(
                ConnectParam.newBuilder()
                        .withHost(milvusHost)
                        .withPort(19530)
                        .build()
        );
    }

//    @Bean
//    Word2Vec word2Vec()  {
//        log.info("Start loading model ...");
//        final String pathToW2V = "GoogleNews-vectors-negative300.bin.gz";
//        final File gModel = new File(pathToW2V);
//        Word2Vec word2Vec = WordVectorSerializer.readWord2VecModel(gModel, true);
//        log.info("Model loaded ..");
//        return word2Vec;
//    }

    @Bean
    TokenizerFactory tokenizerFactory() {
        final TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        final TokenPreProcess preProcess = new CommonPreprocessor();
        tokenizerFactory.setTokenPreProcessor(preProcess);
        return tokenizerFactory;
    }
}
