package com.mskim.search.place.app.place.support.client;

import com.mskim.search.place.support.rest.client.RestClientTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class KakaoMapSearchRestClient extends RestClientTemplate {
    private static final String KAKAO_SEARCH_PLACE_PATH = "/v2/local/search/keyword.json";

    public KakaoMapSearchRestClient(@Value("${kakao.client.key}") String kakaoClientKey,
                                    @Value("${kakao.domain.map}") String kakaoMapDomain) {
        super();

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json;charset=UTF-8");
        headers.put("Authorization", "KakaoAK " + kakaoClientKey);

        this.setDomainUrl(kakaoMapDomain)
            .setPath(KAKAO_SEARCH_PLACE_PATH)
            .setHeaders(headers);
    }

    @Override
    protected Map parseListAsEntity(ResponseEntity<Map> response) {
        Map<?, ?> result = (Map<?, ?>) response.getBody();

        return result;
    }

}
