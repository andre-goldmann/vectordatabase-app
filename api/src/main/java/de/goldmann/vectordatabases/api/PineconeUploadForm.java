package de.goldmann.vectordatabases.api;

public class PineconeUploadForm {
    private String apiKey;
    private String modelName;
    private String indexName;
    private String environment;
    private String metric;
    private String url;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "PineconeUploadForm{" +
                "apiKey='" + apiKey + '\'' +
                ", modelName='" + modelName + '\'' +
                ", indexName='" + indexName + '\'' +
                ", environment='" + environment + '\'' +
                ", metric='" + metric + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
