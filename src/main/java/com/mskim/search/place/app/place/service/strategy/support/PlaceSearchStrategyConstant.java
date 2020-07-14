package com.mskim.search.place.app.place.service.strategy.support;

import java.util.HashMap;
import java.util.Map;

public enum PlaceSearchStrategyConstant {
    KAKAO("kakaoSearchStrategy", "kakao"),
    NAVER("naverSearchStrategy", "naver"),
    GOOGLE("googleSearchStrategy", "google");

    private static final Map<String, PlaceSearchStrategyConstant> BY_STRATEGY = new HashMap<>();
    private static final Map<String, PlaceSearchStrategyConstant> BY_NAME = new HashMap<>();
    static {
        for (PlaceSearchStrategyConstant constant : values()) {
            BY_NAME.put(constant.name, constant);
            BY_STRATEGY.put(constant.strategy, constant);
        }
    }

    public final String name;
    public final String strategy;

    PlaceSearchStrategyConstant(String strategy, String name) {
        this.strategy = strategy;
        this.name = name;
    }

    public static PlaceSearchStrategyConstant valueOfName(String name) {
        return BY_NAME.get(name);
    }

    public String toStrategyString() {
        return this.strategy;
    }
}
