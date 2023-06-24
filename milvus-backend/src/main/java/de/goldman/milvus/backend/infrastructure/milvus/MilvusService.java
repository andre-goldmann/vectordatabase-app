package de.goldman.milvus.backend.infrastructure.milvus;

import de.goldman.milvus.backend.infrastructure.rest.FastApiConnectController;
import de.goldman.milvus.backend.interfaces.CollectionsFields;
import de.goldman.milvus.backend.interfaces.MilvusInsertRequest;
import de.goldman.milvus.backend.interfaces.MilvusSearchRequest;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.*;
import io.milvus.param.*;
import io.milvus.param.collection.*;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.QueryParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.response.DescCollResponseWrapper;
import io.milvus.response.GetCollStatResponseWrapper;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.SearchResultsWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MilvusService {

    private static final Integer VECTOR_DIM = 384;
    private static final String DOMAIN_FIELD = "domain";

    private static final String VECTOR_FIELD = "vector";
    private static final String INDEX_NAME = "domainIndex";
    private static final IndexType INDEX_TYPE = IndexType.IVF_FLAT;
    private static final String INDEX_PARAM = "{\"nlist\":128}";

    private static final Integer SEARCH_K = 5;
    private static final String SEARCH_PARAM = "{\"nprobe\":10}";

    @Autowired
    FastApiConnectController fastApiConnectController;

    @Autowired
    MilvusServiceClient milvusClient;

    public R<MutationResult> insertData(final MilvusInsertRequest milvusInsertRequest){

        final List<InsertParam.Field> fields = new ArrayList<>();
        milvusInsertRequest.getFields().forEach((e, v) -> fields.add(new InsertParam.Field(e, v)));

        final InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName(milvusInsertRequest.getCollectionName())
                //.withPartitionName("novel")
                .withFields(fields)
                .build();
        final R<MutationResult> response = this.milvusClient.insert(insertParam);
        handleResponseStatus(response);
        return response;
    }

    public void createIndex(final String collectionName){
        this.milvusClient.createIndex(CreateIndexParam.newBuilder()
                .withCollectionName(collectionName)
                .withFieldName(VECTOR_FIELD)
                .withIndexName(INDEX_NAME)
                .withIndexType(INDEX_TYPE)
                .withMetricType(MetricType.L2)
                .withExtraParam(INDEX_PARAM)
                .withSyncMode(Boolean.TRUE)
                .build());
    }

    public R<RpcStatus> dropCollection(final String collectionName){
        final R<RpcStatus> rpcStatusR = this.milvusClient.dropCollection(
                DropCollectionParam.newBuilder()
                        .withCollectionName(collectionName)
                        .build()
        );
        handleResponseStatus(rpcStatusR);
        return rpcStatusR;
    }

    public R<RpcStatus> createCollection(final String collectionName, final List<MilvusConfigDto> configDtos) {
        Objects.requireNonNull(collectionName);
        Objects.requireNonNull(configDtos);
        if(configDtos.isEmpty()){
            throw new IllegalArgumentException("configDtos cannot be empty!");
        }
        final FieldType fieldTypeVector = FieldType.newBuilder()
                .withName(VECTOR_FIELD)
                .withDataType(DataType.FloatVector)
                .withDimension(VECTOR_DIM)
                .build();
        final CreateCollectionParam.Builder createCollectionReq = CreateCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .withShardsNum(2)
                .addFieldType(fieldTypeVector);

        configDtos.forEach(config -> {
            FieldType.Builder fieldTypeBuilder = FieldType.newBuilder()
                    .withName(config.getName())
                    .withDataType(DataType.valueOf(config.getDataType()))
                    .withPrimaryKey(config.getPrimaryKey())
                    .withAutoID(config.getAutoID());
            if(DataType.VarChar == DataType.valueOf(config.getDataType())){
                // There are other params but for now leave it like this
                fieldTypeBuilder.addTypeParam(Constant.VARCHAR_MAX_LENGTH, "228");
            }

            Map<String, String> params = config.getParams();
            if(params != null) {
                params.forEach(fieldTypeBuilder::addTypeParam);
            }
            FieldType fieldType = fieldTypeBuilder.build();
            createCollectionReq.addFieldType(fieldType);
        });

       final R<RpcStatus> response = this.milvusClient
               .withTimeout(2000, TimeUnit.MILLISECONDS)
               .createCollection(createCollectionReq.build());
        handleResponseStatus(response);
        return response;
    }

    public R<RpcStatus> loadCollection(final String collectionName) {
        log.info("========== loadCollection() ==========");
        final R<RpcStatus> response = milvusClient.loadCollection(LoadCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build());
        handleResponseStatus(response);
        return response;
    }

    public void searchDomainByExpression(final MilvusSearchRequest searchRequest) {
        loadCollection(searchRequest.getCollectionName());
        log.info("========== searchFace() ==========");
        long begin = System.currentTimeMillis();

        final QueryParam queryParam = QueryParam.newBuilder()
                .withCollectionName(searchRequest.getCollectionName())
                .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
                //.withExpr(DOMAIN_FIELD + " like " + expr)
                .withExpr(searchRequest.getSearchStr())
                .withOutFields(searchRequest.getOutFields())
                .withOffset(0L)
                .withLimit(10L)
                .build();
        final R<QueryResults> respQuery = milvusClient.query(queryParam);

        final long end = System.currentTimeMillis();
        final long cost = (end - begin);
        log.info("Search time cost: " + cost + "ms");

        final QueryResultsWrapper wrapperQuery = new QueryResultsWrapper(respQuery.getData());
        log.info(wrapperQuery.getFieldWrapper(DOMAIN_FIELD).getFieldData() + "");
        log.info(wrapperQuery.getFieldWrapper( "employees").getFieldData() + "");
        this.milvusClient.releaseCollection(
                ReleaseCollectionParam.newBuilder()
                        .withCollectionName(searchRequest.getCollectionName())
                        .build());
    }

    public R<SearchResults> searchWithVector(final MilvusSearchRequest searchRequest) {
        // loadCollection() must be called before search()
        loadCollection(searchRequest.getCollectionName());
        log.info("========== searchFace() ==========");
        final long begin = System.currentTimeMillis();

        // Funktioniert nicht
        //String searchExpr = DOMAIN_FIELD + " in " + expr;
        // Does this work?
        //String searchExpr = DOMAIN_FIELD + " like " + expr;
        // Funktioniert
        final String searchExpr = "employees" + " > " + 50;

        final List<List<Float>> vectors = new ArrayList<>();
        vectors.add(this.fastApiConnectController.calculateSenteceVectors(searchRequest.getSearchStr()));

        final SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(searchRequest.getCollectionName())
                .withMetricType(MetricType.L2)
                .withOutFields(searchRequest.getOutFields())
                .withTopK(SEARCH_K)
                .withVectors(vectors)
                .withVectorFieldName(VECTOR_FIELD)
                //.withExpr(searchExpr)
                .withParams(SEARCH_PARAM)
                .withGuaranteeTimestamp(Constant.GUARANTEE_EVENTUALLY_TS)
                .build();

        final R<SearchResults> response = this.milvusClient.search(searchParam);
        final long end = System.currentTimeMillis();
        final long cost = (end - begin);
        log.info("Search time cost: " + cost + "ms");

        handleResponseStatus(response);
        final SearchResultsWrapper wrapper = new SearchResultsWrapper(response.getData().getResults());

        for (int i = 0; i < vectors.size(); ++i) {
            log.info("Search result of No." + i);
            final List<SearchResultsWrapper.IDScore> scores = wrapper.getIDScore(i);
            log.info(scores + "");
            log.info("Output field data for No." + i);
            //System.out.println(wrapper.getFieldData(DOMAIN_FIELD, i));
//            searchRequest.getOutFields().forEach(f -> wrapper.getFieldData(f, i));
            log.info(wrapper.getFieldData(DOMAIN_FIELD, i) + ": " +
                    "employees=" + wrapper.getFieldData("employees" ,i)+
                    ", social=" + wrapper.getFieldData("social" ,i));
        }
        this.milvusClient.releaseCollection(
                ReleaseCollectionParam.newBuilder()
                        .withCollectionName(searchRequest.getCollectionName())
                        .build());
        return response;
    }

    public R<ShowCollectionsResponse> showCollections() {
        final R<ShowCollectionsResponse> response = this.milvusClient.showCollections(ShowCollectionsParam.newBuilder()
                .build());
        handleResponseStatus(response);
        return response;
    }

    public boolean hasCollection(final String collectionName) {
        final R<Boolean> response = this.milvusClient.hasCollection(HasCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build());
        handleResponseStatus(response);
        return response.getData();
    }

    private void handleResponseStatus(final R<?> r) {
        if (r.getStatus() != R.Status.Success.getCode()) {
           throw new RuntimeException(r.getMessage());
        }
    }

    public List<CollectionsFields> collectionDetails(final String collectionName){
        final R<DescribeCollectionResponse> respDescribeCollection = milvusClient.describeCollection(
                // Return the name and schema of the collection.
                DescribeCollectionParam.newBuilder()
                        .withCollectionName(collectionName)
                        .build()
        );
        final DescCollResponseWrapper wrapperDescribeCollection = new DescCollResponseWrapper(respDescribeCollection.getData());

        final List<FieldType> fields = wrapperDescribeCollection.getFields();
        List<CollectionsFields> result = new ArrayList<>();
        fields.forEach(e -> {
            result.add(new CollectionsFields(
                    e.getName(),
                    e.isPrimaryKey(),
                    e.getDescription(),
                    e.getDataType().name(),
                    e.getTypeParams(),
                    e.isAutoID()));
        });
        return result;
    }

    public long colletionEntryCount(final String collectionName) {

        final R<GetCollectionStatisticsResponse> respCollectionStatistics = this.milvusClient.getCollectionStatistics(
                // Return the statistics information of the collection.
                GetCollectionStatisticsParam.newBuilder()
                        .withCollectionName(collectionName)
                        .build()
        );
        return new GetCollStatResponseWrapper(respCollectionStatistics.getData()).getRowCount();
    }

    //    public void search(String collectionName) {
//        final Integer SEARCH_K = 2;// TopK Number of the most similar results to return.
//        final String SEARCH_PARAM = "{\"nprobe\":10, \"offset\":5}"; // Param Search parameter(s) specific to the index.
//        List<String> search_output_fields = List.of("domain");
//        List<List<Float>> search_vectors = List.of(Arrays.asList(0.1f, 0.2f));
//        String searchField = "domain"; //Name of the field to search on.
//        SearchParam searchParam = SearchParam.newBuilder()
//                .withCollectionName(collectionName)
//                .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
//                .withMetricType(MetricType.L2)
//                .withOutFields(search_output_fields)
//                .withTopK(SEARCH_K)
//                .withVectors(search_vectors)
//                .withVectorFieldName(searchField)
//                .withParams(SEARCH_PARAM)
//                .build();
//        R<SearchResults> respSearch = milvusClient.search(searchParam);
//
////        SearchResultsWrapper wrapper = new SearchResultsWrapper(response.getData().getResults());
////        System.out.println("Search results:");
////        for (int i = 0; i < search_vectors.size(); ++i) {
////            List<SearchResultsWrapper.IDScore> scores = results.getIDScore(i);
////            for (int j = 0; j < scores.size(); ++j) {
////                SearchResultsWrapper.IDScore score = scores.get(j);
////                System.out.println("Top " + j + " ID:" + score.getLongID() + " Distance:" + score.getScore());
////            }
////        }
//    }

    //    @Autowired
//    private SiteEntityRepository siteEntityRepository;

//    public void prepateData(final String collectionName){
//
//        List<String> domain_array = new ArrayList<>();
//        List<Integer> employes_count_array = new ArrayList<>();
//        List<Integer> social_intro_array = new ArrayList<>();
//        List<List<Float>> vectors = new ArrayList<>();
//        this.siteEntityRepository.findAll()
//                .forEach(e -> {
//
//                    //vectors.add(loadGoogleNewsModelWithReflection.vectors(e.getDomain()));
//                    domain_array.add(e.getDomain());
//                    if(e.getEmployees() == null){
//                        employes_count_array.add(0);
//                    }else {
//                        employes_count_array.add(e.getEmployees());
//                    }
//                    if(e.getSocial() == null){
//                        social_intro_array.add(0);
//                    }else {
//                        social_intro_array.add(e.getSocial());
//                    }
//
//                });
//
//        insertData(collectionName, domain_array, employes_count_array, social_intro_array, vectors);
//    }

}
