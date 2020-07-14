package com.mskim.search.place.support.rest.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

public abstract class RestClientTemplate {
    private RestTemplate restTemplate;
    private HttpHeaders headers;
    private UriComponentsBuilder uriComponentsBuilder;

    protected RestClientTemplate() {
        this.restTemplate = new RestTemplate();
        this.headers = new HttpHeaders();
        this.uriComponentsBuilder = UriComponentsBuilder.newInstance();
    }

    public RestClientTemplate setHeaders(Map<String, String> headers) {
        headers.forEach((k, v) -> {
            this.headers.add(k, v);
        });

        return this;
    }

    public RestClientTemplate setProtocol(String protocol) {
        uriComponentsBuilder.scheme(protocol);

        return this;
    }

    public RestClientTemplate setDomainUrl(String domainUrl) {
        if (StringUtils.isEmpty(domainUrl)) {
            throw new IllegalArgumentException(domainUrl);
        }

        if (domainUrl.matches("http(s?)://.*")) {
            uriComponentsBuilder.uri(URI.create(domainUrl));
        } else {
            uriComponentsBuilder.host(domainUrl);
        }

        return this;
    }

    public RestClientTemplate setPath(String path) {
        uriComponentsBuilder.path(path);

        return this;
    }

    public RestClientTemplate setParam(String key, String value) {
        uriComponentsBuilder.replaceQueryParam(key, value);

        return this;
    }

    public RestClientTemplate setParam(String key, @Nullable Collection<?> values) {
        uriComponentsBuilder.replaceQueryParam(key, values);

        return this;
    }

    public RestClientTemplate setParams(MultiValueMap<String, String> params) {
        uriComponentsBuilder.replaceQueryParams(params);

        return this;
    }

    public RestClientTemplate setUriVariables(Map<String, Object> variables) {
        uriComponentsBuilder.uriVariables(variables);

        return this;
    }

    public String buildToUriString() {
        return uriComponentsBuilder.build().toUriString();
    }

    public Object getListAsEntity(@Nullable Object... urlValues) {
        ResponseEntity<Map> response = callApi(HttpMethod.GET, this.buildToUriString(), headers, Map.class, urlValues);

        return parseListAsEntity(response);
    }

    public <T> T getListAsObject(Class<T> responseObjectType, @Nullable Object... urlValues) {
        T response = this.restTemplate.getForObject(this.buildToUriString(), responseObjectType, urlValues);

        return response;
    }

    protected abstract Object parseListAsEntity(ResponseEntity<Map> response);

    private <T> ResponseEntity<T> callApi(HttpMethod method, String endpoint, HttpHeaders headers, Class<T> responseType, @Nullable Object... urlValues) {
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<T> response = this.restTemplate.exchange(endpoint, method, entity, responseType, urlValues);

        return response;
    }

}
