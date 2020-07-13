package com.mskim.search.place.app.place.service.strategy.support;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public enum PlaceSearchStrategyConstant {
    KAKAO("kakao"),
    GOOGLE("google"),
    NAVER("naver");

    private String value;

    PlaceSearchStrategyConstant(String value) {
        this.value = value;
    }
}
