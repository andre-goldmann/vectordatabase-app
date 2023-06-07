package de.goldman.csv.mataoln.infrastructure.milvus;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.stopwords.StopWords;
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
public class LoadGoogleNewsModelWithReflection {

//    @Autowired
//    Word2Vec word2Vec;
//
//    @Autowired
//    TokenizerFactory tokenizerFactory;

    private static final Pattern charsPunctuationPattern = Pattern.compile("[\\d:,\"\'\\`\\_\\|?!\n\r@;]+");

    private Collection<String> clean(final String text) {

        String input_text = charsPunctuationPattern.matcher(text.trim().toLowerCase()).replaceAll("");
        //replace text between {},[],() including them
        input_text = input_text.replaceAll("\\{.*?\\}", "");
        input_text = input_text.replaceAll("\\[.*?\\]", "");
        input_text = input_text.replaceAll("\\(.*?\\)", "");
        input_text = input_text.replaceAll("[^A-Za-z0-9(),!?@\'\\`\"\\_\n]", " ");
        input_text = input_text.replaceAll("[/]", " ");
        input_text = input_text.replaceAll(";", " ");
        //Collect all tokens into labels collection.
        Collection<String> labels = Arrays.asList(input_text.split(" "))
                .parallelStream()
                .filter(label -> label.length() > 0).collect(Collectors.toList());
        //get from standard text files available for Stopwords. e.g https://algs4.cs.princeton.edu/35applications/stopwords.txt
        labels = labels
                .parallelStream()
                .filter(label -> !StopWords.getStopWords().contains(label.trim()))
                .collect(Collectors.toList());

        return labels;
    }

//    public List<Float> vectors(final String text) {
//        List<Float> vectorData = new ArrayList<>();
//        final Collection<String> tokens = clean(text);
//        final TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
//        final TokenPreProcess preProcess = new CommonPreprocessor();
//        tokenizerFactory.setTokenPreProcessor(preProcess);
//
//        for (String token : tokens) {
//            Tokenizer tokenizer = tokenizerFactory.create(token);
//            List<String> tokenList = tokenizer.getTokens();
//
//            for (String t : tokenList) {
//                if (word2Vec.hasWord(t)) {
//                    final INDArray r = word2Vec.getWordVectorMatrix(token);
//                    float[] arrayFloat = r.dup().data().asFloat();
//                    for (float f : arrayFloat) {
//                        vectorData.add(f);
//                    }
//
//                }
//            }
//        }
//
//        return vectorData;
//    }
}