package com.mskim.search.place.app.place.service.strategy.support;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public enum PlaceSearchCacheConstant {
    PLACE_CACHE_NAME("place"),
    PLACES_CACHE_NAME("places");

    private String value;

    PlaceSearchCacheConstant(String value) {
        this.value = value;
    }
}
