package com.mskim.search.place.app.place.service;

import com.mskim.search.place.app.keyword.service.KeywordService;
import com.mskim.search.place.app.place.dto.Place;
import com.mskim.search.place.app.place.dto.PlaceDto;
import com.mskim.search.place.app.place.service.interfaces.PlaceSearchableStrategy;
import com.mskim.search.place.app.place.service.strategy.KakaoSearchStrategy;
import com.mskim.search.place.app.place.service.strategy.NaverSearchStrategy;
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

    private PlaceSearchStrategyConstant strategyConstant;


    @Autowired
    public PlaceService(KeywordService keywordService, ApplicationContext context) {
        this.context = context;
        this.keywordService = keywordService;
        this.strategyConstant = PlaceSearchStrategyConstant.KAKAO;
        this.searchStrategy = (KakaoSearchStrategy) context.getBean(strategyConstant.toStrategyString());
    }

    public void applyStrategy(PlaceSearchStrategyConstant strategyType) {
        if (this.strategyConstant == strategyType) {
            return;
        }

        switch (strategyType) {
            case KAKAO:
                this.searchStrategy = context.getBean(strategyConstant.toStrategyString(), KakaoSearchStrategy.class);
                break;
            case NAVER:
                this.searchStrategy = context.getBean(strategyConstant.toStrategyString(), NaverSearchStrategy.class);;
                break;
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
