package com.mskim.search.place.app.place;

import com.mskim.search.place.app.keyword.repository.KeywordRepository;
import com.mskim.search.place.app.keyword.service.KeywordService;
import com.mskim.search.place.app.place.dto.Place;
import com.mskim.search.place.app.place.dto.PlaceDto;
import com.mskim.search.place.app.place.service.PlaceService;
import com.mskim.search.place.app.place.service.strategy.KakaoSearchStrategy;
import com.mskim.search.place.app.place.support.client.KakaoMapSearchRestClient;
import com.mskim.search.place.support.SampleApiResponseMaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = { KakaoSearchStrategy.class, PlaceService.class })
class PlaceServiceTest {
    @Autowired
    private PlaceService placeService;

    @MockBean
    private KakaoMapSearchRestClient restClient;
    @MockBean
    private KeywordService keywordService;
    @MockBean
    private KeywordRepository keywordRepository;
    @MockBean
    private CacheManager cacheManager;
    @Mock
    private Cache cache;

    private String keyword;
    private int page;
    private SampleApiResponseMaker dummyMaker;
    private Map dummyResponse;
    private List<Place> placeDto;

    @BeforeEach
    void setUp() throws IOException {
        dummyMaker = new SampleApiResponseMaker();
        dummyResponse = dummyMaker.getResponseBody();
        placeDto = dummyMaker.convertMapToDto(dummyResponse);
        keyword = "서울역";
        page = 1;
    }

    @Test
    void PlaceService_장소_조회_캐시_미사용() throws IOException {
        // given
        // setUp()
        given(cacheManager.getCache(any(String.class)))
                .willReturn(new ConcurrentMapCache("test"));
        given(restClient.setParams(any())).willReturn(restClient);
        given(restClient.getListAsEntity()).willReturn(dummyResponse);

        // when
        PlaceDto dto = placeService.retrievePlace(keyword, page);
        System.out.println(dto);

        // then
        verify(restClient, times(2)).setParams(any());
        verify(restClient, times(2)).getListAsEntity();

        then(dto.getPlaces()).isNotNull();
        then(dto.getPlaces().size()).isEqualTo(15);

        then(dto.getPager()).isNotNull();
        then(dto.getPager().getTotalItemCount()).isNotZero();
        then(dto.getPager().getTotalPageCount()).isGreaterThanOrEqualTo(3);
    }

    @Test
    void PlaceService_장소_조회_캐시_사용() throws IOException {
        // given
        // setUp()
        cache = new ConcurrentMapCache("test");
        cache.put(keyword + "_" + page, placeDto);
        given(cacheManager.getCache(any(String.class)))
                .willReturn(cache);
        given(restClient.setParams(any())).willReturn(restClient);
        given(restClient.getListAsEntity()).willReturn(dummyResponse);

        // when
        PlaceDto dto = placeService.retrievePlace(keyword, page);
        System.out.println(dto);

        // then
        then(dto.getPlaces()).isNotNull();
        then(dto.getPlaces().size()).isEqualTo(15);

        then(dto.getPager()).isNotNull();
        then(dto.getPager().getTotalItemCount()).isNotZero();
        then(dto.getPager().getTotalPageCount()).isGreaterThanOrEqualTo(3);
    }

    @Test
    void PlaceService_장소_조회_검색어_저장() {
        // given
        String newKeyword = "남대문";
        given(cacheManager.getCache(any(String.class)))
                .willReturn(new ConcurrentMapCache("test"));
        given(restClient.setParams(any())).willReturn(restClient);
        given(restClient.getListAsEntity()).willReturn(dummyResponse);
        given(keywordService.storeKeyword(newKeyword)).willReturn(true);

        // when
        PlaceDto dto = placeService.retrievePlace(newKeyword, page);

        // then
        verify(keywordService).storeKeyword(newKeyword);

        then(dto.getPlaces()).isNotNull();
        then(dto.getPlaces().size()).isEqualTo(15);

        then(dto.getPager()).isNotNull();
        then(dto.getPager().getTotalItemCount()).isNotZero();
        then(dto.getPager().getTotalPageCount()).isGreaterThanOrEqualTo(3);
    }

    @Test
    void PlaceService_장소_상세_조회_캐시_사용() {
        // given
        int placeId = 9113903;

        cache = new ConcurrentMapCache("test");
        cache.put(placeId, placeDto.get(0));
        given(cacheManager.getCache(any(String.class)))
                .willReturn(cache);

        // when
        Place place = placeService.retrievePlaceDetail(placeId, null);

        then(place).isNotNull();
        then(place.getId()).isEqualTo(placeId);
        then(place.getShortcutLink()).isNotBlank();

        System.out.println(place.toString());
    }

    @Test
    void PlaceService_장소_상세_조회_캐시_실패() {
        // given
        int placeId = 9113903;

        cache = new ConcurrentMapCache("test");
        cache.put(keyword + "_" + page, placeDto);
        given(cacheManager.getCache("place"))
                .willReturn(new ConcurrentMapCache("test"));
        given(cacheManager.getCache("places"))
                .willReturn(cache);

        // when
        HttpSession session = new MockHttpSession();
        session.setAttribute("keyword", keyword);
        session.setAttribute("page", page);
        Place place = placeService.retrievePlaceDetail(placeId, session);

        // then
        then(place).isNotNull();
        then(place.getId()).isEqualTo(placeId);
        then(place.getShortcutLink()).isNotBlank();

        System.out.println(place.toString());
    }


}