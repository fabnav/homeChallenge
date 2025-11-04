package com.homechallenge.pokedex.helper;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Helper class for setting up HTTP request mocks in tests
 */
@Component
@Slf4j
public class HttpRequestHelper {
    
    private final RestClient restClient;
    private final RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;
    private final RestClient.ResponseSpec responseSpec;
    
    public HttpRequestHelper(RestClient restClient,
                            RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec,
                            RestClient.ResponseSpec responseSpec) {
        this.restClient = restClient;
        this.requestHeadersUriSpec = requestHeadersUriSpec;
        this.responseSpec = responseSpec;
    }
    
    /**
     * Mocks a GET request to the specified URI with the given response (Map type)
     *
     * @param uri The URI pattern (e.g., "/pokemon-species/{name}")
     * @param uriVariable The variable to replace in the URI pattern
     * @param response The response to return as Map
     */
    public void mockGetRequestMap(String uri, String uriVariable, Map<String, Object> response) {
        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersUriSpec).when(requestHeadersUriSpec).uri(eq(uri), eq(uriVariable));
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(eq(Map.class))).thenReturn(response);
    }
}

