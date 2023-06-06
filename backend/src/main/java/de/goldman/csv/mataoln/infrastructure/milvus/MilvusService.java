package de.goldman.csv.mataoln.infrastructure.milvus;

import de.goldman.csv.mataoln.domain.model.SiteEntityRepository;
import de.goldman.csv.mataoln.infrastructure.rest.FastApiConnectController;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.*;
import io.milvus.param.*;
import io.milvus.param.collection.*;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.QueryParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.SearchResultsWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.*;

@Service
public class MilvusService {
    public static final String COLLECTION_NAME = "book";
//    private static final String ID_FIELD = "userID";

    private static final Integer VECTOR_DIM = 384;
    private static final String DOMAIN_FIELD = "domain";
//    private static final String PROFILE_FIELD = "userProfile";
//    private static final Integer BINARY_DIM = 128;
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

    @Autowired
    private SiteEntityRepository siteEntityRepository;

    public void prepateData(final String collectionName){

        List<String> domain_array = new ArrayList<>();
        List<Integer> employes_count_array = new ArrayList<>();
        List<Integer> social_intro_array = new ArrayList<>();
        List<List<Float>> vectors = new ArrayList<>();
        this.siteEntityRepository.findAll()
                .forEach(e -> {

                    //vectors.add(loadGoogleNewsModelWithReflection.vectors(e.getDomain()));
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

        insertData(collectionName, domain_array, employes_count_array, social_intro_array, vectors);
    }

    public void insertData(String collectionName,
                            List<String> domain_array,
                            List<Integer> employes_count_array,
                            List<Integer> social_intro_array,
                            List<List<Float>> vectors){

        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field("domain", domain_array));
        fields.add(new InsertParam.Field("employees", employes_count_array));
        fields.add(new InsertParam.Field("social", social_intro_array));
        fields.add(new InsertParam.Field(VECTOR_FIELD, vectors));

        InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName(collectionName)
                //.withPartitionName("novel")
                .withFields(fields)
                .build();
        R<MutationResult> response = this.milvusClient.insert(insertParam);
        handleResponseStatus(response);
        //create_index() can only be applied to `FloatVector` and `BinaryVector` fields.

    }

    public void createIndex(){
        this.milvusClient.createIndex(CreateIndexParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withFieldName(VECTOR_FIELD)
                .withIndexName(INDEX_NAME)
                .withIndexType(INDEX_TYPE)
                .withMetricType(MetricType.L2)
                .withExtraParam(INDEX_PARAM)
                .withSyncMode(Boolean.TRUE)
                .build());
    }

    public Integer dropCollection(String collectionName){
        return milvusClient.dropCollection(
                DropCollectionParam.newBuilder()
                        .withCollectionName(collectionName)
                        .build()
        ).getStatus();
    }

    public void setup(String collectionName) {
        FieldType fieldType1 = FieldType.newBuilder()
                .withName("domain")
                .withDataType(DataType.VarChar)
                .addTypeParam(Constant.VARCHAR_MAX_LENGTH, "228")
                .withPrimaryKey(true)
                .withAutoID(false)
                .build();
        FieldType fieldType2 = FieldType.newBuilder()
                .withName("employees")
                .withDataType(DataType.Int32)
                .build();
        FieldType fieldType3 = FieldType.newBuilder()
                .withName("social")
                .withDataType(DataType.Int32)
                //.withDimension(2)
                .build();

        FieldType fieldTypeVector = FieldType.newBuilder()
                .withName(VECTOR_FIELD)
                .withDescription("sites embedding")
                .withDataType(DataType.FloatVector)
                .withDimension(VECTOR_DIM)
                .build();

        CreateCollectionParam createCollectionReq = CreateCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .withDescription("Test book search")
                .withShardsNum(2)
                .addFieldType(fieldTypeVector)
                .addFieldType(fieldType1)
                .addFieldType(fieldType2)
                .addFieldType(fieldType3)
                .build();

        R<RpcStatus> response = milvusClient.withTimeout(2000, TimeUnit.MILLISECONDS)
                .createCollection(createCollectionReq);
        handleResponseStatus(response);
        System.out.println(response);
    }

    public R<RpcStatus> loadCollection() {
        System.out.println("========== loadCollection() ==========");
        R<RpcStatus> response = milvusClient.loadCollection(LoadCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .build());
        handleResponseStatus(response);
        System.out.println(response);
        return response;
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

    public void searchDomainByExpression(String expr) {
        loadCollection();
        System.out.println("========== searchFace() ==========");
        long begin = System.currentTimeMillis();

        final List<String> query_output_fields = Arrays.asList(DOMAIN_FIELD, "employees", "social");
        final QueryParam queryParam = QueryParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
                .withExpr(DOMAIN_FIELD + " like " + expr)
                .withOutFields(query_output_fields)
                .withOffset(0L)
                .withLimit(10L)
                .build();
        final R<QueryResults> respQuery = milvusClient.query(queryParam);

        long end = System.currentTimeMillis();
        long cost = (end - begin);
        System.out.println("Search time cost: " + cost + "ms");

        QueryResultsWrapper wrapperQuery = new QueryResultsWrapper(respQuery.getData());
        System.out.println(wrapperQuery.getFieldWrapper(DOMAIN_FIELD).getFieldData());
        System.out.println(wrapperQuery.getFieldWrapper( "employees").getFieldData());
        milvusClient.releaseCollection(
                ReleaseCollectionParam.newBuilder()
                        .withCollectionName(COLLECTION_NAME)
                        .build());

    }

    public R<SearchResults> searchWithVector(String expr) {
        // loadCollection() must be called before search()
        loadCollection();
        System.out.println("========== searchFace() ==========");
        long begin = System.currentTimeMillis();

        List<String> outFields = Arrays.asList(DOMAIN_FIELD, "employees", "social");//Collections.singletonList(DOMAIN_FIELD);
        //List<List<Float>> vectors = generateFloatVectors(5);
        // Funktioniert nicht
        //String searchExpr = DOMAIN_FIELD + " in " + expr;
        // Funktioniert
        String searchExpr = "employees" + " > " + 50;

        final List<List<Float>> vectors = new ArrayList<>();
        vectors.add(fastApiConnectController.calculateSenteceVectors(expr));

        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withMetricType(MetricType.L2)
                .withOutFields(outFields)
                .withTopK(SEARCH_K)
                .withVectors(vectors)
                .withVectorFieldName(VECTOR_FIELD)
                //.withExpr(searchExpr)
                .withParams(SEARCH_PARAM)
                .withGuaranteeTimestamp(Constant.GUARANTEE_EVENTUALLY_TS)
                .build();

        R<SearchResults> response = milvusClient.search(searchParam);
        long end = System.currentTimeMillis();
        long cost = (end - begin);
        System.out.println("Search time cost: " + cost + "ms");

        handleResponseStatus(response);
        SearchResultsWrapper wrapper = new SearchResultsWrapper(response.getData().getResults());
        for (int i = 0; i < vectors.size(); ++i) {
            System.out.println("Search result of No." + i);
            List<SearchResultsWrapper.IDScore> scores = wrapper.getIDScore(i);
            System.out.println(scores);
            System.out.println("Output field data for No." + i);
            //System.out.println(wrapper.getFieldData(DOMAIN_FIELD, i));

            System.out.println(wrapper.getFieldData(DOMAIN_FIELD, i) + ": " +
                    "employees=" + wrapper.getFieldData("employees" ,i)+
                    ", social=" + wrapper.getFieldData("social" ,i));
        }
        milvusClient.releaseCollection(
                ReleaseCollectionParam.newBuilder()
                        .withCollectionName(COLLECTION_NAME)
                        .build());
        return response;
    }

    public R<ShowCollectionsResponse> showCollections() {
        System.out.println("========== showCollections() ==========");
        R<ShowCollectionsResponse> response = milvusClient.showCollections(ShowCollectionsParam.newBuilder()
                .build());
        handleResponseStatus(response);
        System.out.println(response);
        return response;
    }


    private void handleResponseStatus(R<?> r) {
        if (r.getStatus() != R.Status.Success.getCode()) {
           throw new RuntimeException(r.getMessage());
        }
    }

    public boolean hasCollection() {
        System.out.println("========== hasCollection() ==========");
        R<Boolean> response = milvusClient.hasCollection(HasCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .build());
        handleResponseStatus(response);
        System.out.println(response);
        return response.getData();
    }
}
