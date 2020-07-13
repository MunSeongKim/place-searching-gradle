package com.mskim.search.place.app.place.service;

import com.mskim.search.place.app.keyword.service.KeywordService;
import com.mskim.search.place.app.place.dto.Place;
import com.mskim.search.place.app.place.dto.PlaceDto;
import com.mskim.search.place.app.place.service.interfaces.PlaceSearchableStrategy;
import com.mskim.search.place.app.place.service.strategy.KakaoSearchStrategy;
import com.mskim.search.place.app.place.service.strategy.support.PlaceSearchStrategyConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class PlaceService {
    private final KeywordService keywordService;

    private ApplicationContext context;
    private PlaceSearchableStrategy searchStrategy;

    private String strategyName;


    @Autowired
    public PlaceService(KeywordService keywordService, ApplicationContext context) {
        this.context = context;
        this.keywordService = keywordService;
        this.searchStrategy = (KakaoSearchStrategy) context.getBean("kakaoSearchStrategy");
        this.strategyName = PlaceSearchStrategyConstant.KAKAO.value();
    }

    public void applyStrategy(String strategyName) {
        if (this.strategyName.equals(strategyName)) {
            return;
        }

        context.getBean("kakaoSearchStrategy");
        switch (strategyName) {
            case "kakao":
                this.searchStrategy = (KakaoSearchStrategy) context.getBean("kakaoSearchStrategy");
        }
    }

    public Place retrievePlaceDetail(int placeId, HttpSession session) {
        return searchStrategy.searchById(placeId, session);
    }

    public PlaceDto retrievePlace(String placeName, int page) {
        PlaceDto placeDto = searchStrategy.searchByPlaceName(placeName, page);

        if (searchStrategy.isNewKeyword()) {
            this.keywordService.storeKeyword(placeName);
        }

        return placeDto;
    }

}
