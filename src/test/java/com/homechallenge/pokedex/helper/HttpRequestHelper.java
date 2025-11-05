package com.homechallenge.pokedex.helper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

/** Helper class for setting up HTTP request mocks in tests */
@Component
@Slf4j
public class HttpRequestHelper {

  private final RestClient restClient;
  private final RestClient.RequestHeadersUriSpec<?> getSpec;
  private final RestClient.ResponseSpec getResponseSpec;

  private final RestClient.RequestBodyUriSpec postUriSpec;
  private final RestClient.RequestBodySpec postBodySpec; // nuovo
  private final RestClient.ResponseSpec postResponseSpec;

  public HttpRequestHelper(
      RestClient restClient,
      RestClient.RequestHeadersUriSpec<?> getSpec,
      RestClient.ResponseSpec getResponseSpec) {
    this.restClient = restClient;
    this.getSpec = getSpec;
    this.getResponseSpec = getResponseSpec;
    this.postUriSpec = null;
    this.postBodySpec = null;
    this.postResponseSpec = null;
  }

  public HttpRequestHelper(
      RestClient restClient,
      RestClient.RequestBodyUriSpec postUriSpec,
      RestClient.RequestBodySpec postBodySpec,
      RestClient.ResponseSpec postResponseSpec) {
    this.restClient = restClient;
    this.postUriSpec = postUriSpec;
    this.postBodySpec = postBodySpec;
    this.postResponseSpec = postResponseSpec;
    this.getSpec = null;
    this.getResponseSpec = null;
  }

  public void mockGetRequestMap(String uriTemplate, Object uriVar, Map<String, Object> response) {
    doReturn(getSpec).when(restClient).get();
    doReturn(getSpec).when(getSpec).uri(eq(uriTemplate), eq(uriVar));
    doReturn(getResponseSpec).when(getSpec).retrieve();
    doReturn(response).when(getResponseSpec).body(eq(Map.class));
  }

  public void mockPostRequestMap(String uriTemplate, String pathVar, Map<String, Object> response) {
    doReturn(postUriSpec).when(restClient).post();
    doReturn(postBodySpec).when(postUriSpec).uri(eq(uriTemplate), eq(pathVar));
    doReturn(postBodySpec).when(postBodySpec).body(any(MultiValueMap.class));
    doReturn(postResponseSpec).when(postBodySpec).retrieve();
    doReturn(response).when(postResponseSpec).body(eq(Map.class));
  }

  public static HttpRequestHelper forGet(
      RestClient c, RestClient.RequestHeadersUriSpec<?> s, RestClient.ResponseSpec r) {
    return new HttpRequestHelper(c, s, r);
  }

  public static HttpRequestHelper forPost(
      RestClient c,
      RestClient.RequestBodyUriSpec uriSpec,
      RestClient.RequestBodySpec bodySpec,
      RestClient.ResponseSpec r) {
    return new HttpRequestHelper(c, uriSpec, bodySpec, r);
  }
}
