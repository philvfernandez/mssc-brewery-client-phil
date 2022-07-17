package guru.springframework.msscbreweryclient.web.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BlockingRestTemplateCustomizer implements RestTemplateCustomizer {

    private final Integer maxTotalConnetions;
    private final Integer defaultMaxTotalCOnnections;
    private final Integer connectionRequestTimeout;
    private final Integer socketTomeout;


    public BlockingRestTemplateCustomizer(@Value("${client.maxtotalconnections}") Integer maxTotalConnetions,
                                          @Value("${client.default.max.per.route}") Integer defaultMaxTotalCOnnections,
                                          @Value("${client.connection.request.timeout}") Integer connectionRequestTimeout,
                                          @Value("${client.socket.timeout}") Integer socketTomeout) {
        this.maxTotalConnetions = maxTotalConnetions;
        this.defaultMaxTotalCOnnections = defaultMaxTotalCOnnections;
        this.connectionRequestTimeout = connectionRequestTimeout;
        this.socketTomeout = socketTomeout;
    }

    /*
    @Value("${client.max.total.connections}")
    Integer CLIENT_MAX_TOTAL_CONNECTIONS;

    @Value("${client.default.max.per.route}")
    Integer CLIENT_DEFAULT_MAX_PER_ROUTE;

    @Value("${client.connection.request.timeout}")
    Integer CLIENT_CONNECTION_REQUEST_TIMEOUT;

    @Value("${client.socket.timeout}")
    Integer CLIENT_SOCKET_TIMEOUT; */



    public ClientHttpRequestFactory clientHttpRequestFactory() {

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxTotalConnetions); //max total connections

        connectionManager.setDefaultMaxPerRoute(defaultMaxTotalCOnnections);

        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectionRequestTimeout(connectionRequestTimeout) //in msecs
                .setSocketTimeout(socketTomeout) //in msecs
                .build();

        CloseableHttpClient httpClient = HttpClients
                .custom()
                .setConnectionManager(connectionManager)
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.setRequestFactory(this.clientHttpRequestFactory());

    }
}
