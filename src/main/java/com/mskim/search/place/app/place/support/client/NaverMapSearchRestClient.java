package com.mskim.search.place.app.place.support.client;

import com.mskim.search.place.support.rest.client.RestClientTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component(value = "naverMapSearchRestClient")
public class NaverMapSearchRestClient extends RestClientTemplate {
    private static final String NAVER_SEARCH_PLACE_PATH = "/v1/search/local.json";

    public NaverMapSearchRestClient(@Value("${naver.client.key}") String naverClientKey,
                                    @Value("${naver.client.secret}") String naverClientSecret,
                                    @Value("${naver.domain.map}") String naverMapDomain) {
        super();

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json;charset=UTF-8");
        headers.put("X-Naver-Client-Id", naverClientKey);
        headers.put("X-Naver-Client-Secret", naverClientSecret);

        this.setDomainUrl(naverMapDomain)
            .setPath(NAVER_SEARCH_PLACE_PATH)
            .setHeaders(headers);
    }

    @Override
    protected Map parseListAsEntity(ResponseEntity<Map> response) {
        Map<?, ?> result = (Map<?, ?>) response.getBody();

        return result;
    }

}
