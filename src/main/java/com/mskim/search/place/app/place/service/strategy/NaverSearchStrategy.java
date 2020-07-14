package com.mskim.search.place.app.place.service.strategy;

import com.mskim.search.place.app.place.dto.Place;
import com.mskim.search.place.app.place.dto.PlaceDto;
import com.mskim.search.place.app.place.dto.page.PlacePager;
import com.mskim.search.place.app.place.service.interfaces.PlaceSearchableStrategy;
import com.mskim.search.place.app.place.service.strategy.support.PlaceSearchCacheConstant;
import com.mskim.search.place.app.place.support.client.KakaoMapSearchRestClient;
import com.mskim.search.place.app.place.support.client.NaverMapSearchRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class NaverSearchStrategy implements PlaceSearchableStrategy {
    private static final String PLACE_CACHE_NAME = PlaceSearchCacheConstant.PLACE_CACHE_NAME.value();
    private static final String PLACES_CACHE_NAME = PlaceSearchCacheConstant.PLACES_CACHE_NAME.value();

    private static final String DISPLAY_COUNT = "5";

    private final CacheManager cacheManager;
    private final NaverMapSearchRestClient client;

    private boolean isNewKeyword;
    private String keyword;
    private PlacePager placePager;

    @Autowired
    public NaverSearchStrategy(CacheManager cacheManager, NaverMapSearchRestClient naverMapSearchRestclient) {
        this.cacheManager = cacheManager;
        this.client = naverMapSearchRestclient;
        placePager = initializePager();
    }

    @Override
    public Place searchById(int placeId, HttpSession session) {
        Place place = cacheManager.getCache(PLACE_CACHE_NAME)
                .get(placeId, Place.class);

        if (session != null && place == null) {
            String keyword = session.getAttribute("keyword").toString();
            List cacheData = cacheManager.getCache(PLACES_CACHE_NAME)
                    .get(keyword, List.class);
            for (Object item : cacheData) {
                if (item instanceof Place) {
                    Place placeItem = (Place) item;
                    if (placeItem.getId() == placeId) {
                        place = placeItem;
                    }
                }
            }
        }

        return place;
    }

    @Override
    public PlaceDto searchByPlaceName(String placeName, int page) {
        this.updateKeywordState(placeName);

        List<Place> places = getPlacesFromApi(placeName);

        if (places.isEmpty()) {
            return PlaceDto.builder()
                    .places(places)
                    .pager(null)
                    .build();
        }

        int itemIndex = placePager.getStartItemNumber();
        for (Place place : places) {
            place.assignItemIndex(itemIndex++);
        }

        return PlaceDto.builder()
                .places(places)
                .pager(this.placePager)
                .build();
    }

    @Override
    public boolean isNewKeyword() {
        return isNewKeyword;
    }

    private List<Place> getPlacesFromApi(String placeName) {
        String cacheItemKey = getPlacesCacheKey(placeName);
        Cache cache = cacheManager.getCache(PLACES_CACHE_NAME);
        List cachePlaces = cache.get(cacheItemKey, List.class);

        List<Place> places = new ArrayList<>();
        if (cachePlaces != null) {
            for (Object item : cachePlaces) {
                if (item instanceof Place) {
                    places.add((Place) item);
                }
            }

            return places;
        }

        MultiValueMap<String, String> params = createParams(placeName);
        Map response = (Map) client.setParams(params).getListAsEntity();

        places = parsePlaceApiResponse(response);
        cache.put(cacheItemKey, places);

        return places;
    }

    private List<Place> parsePlaceApiResponse(Map response) {
        @SuppressWarnings("unchecked")
        List<Map<String, String>> placeList = (List<Map<String, String>>) response.get("documents");


        List<Place> places = new ArrayList<>();
        for (Map<String, String> item: placeList) {
            int id = Integer.parseInt(item.get("id"));
            Place place = Place.builder()
                    .id(id)
                    .name(item.get("place_name"))
                    .category(item.get("category_name"))
                    .address(item.get("address_name"))
                    .roadAddress(item.get("road_address_name"))
                    .phone(item.get("phone"))
                    .longitude(Double.valueOf(item.get("x")))
                    .latitude(Double.valueOf(item.get("y")))
                    .shortcutLink("")
                    .build();
            cacheManager.getCache(PLACE_CACHE_NAME).put(id, place);
            places.add(place);
        }

        return places;
    }

    private MultiValueMap<String, String> createParams(String placeName) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("query", placeName);
        params.add("display", DISPLAY_COUNT);

        return params;
    }

    private String getPlacesCacheKey(String keyword) {
        return keyword;
    }

    private PlacePager initializePager() {
        return PlacePager.builder()
                .displayItemCount(Integer.parseInt(DISPLAY_COUNT))
                .totalItemCount(Integer.parseInt(DISPLAY_COUNT))
                .build().update();
    }

    private void updateKeywordState(String newKeyword) {
        if (newKeyword.equals(keyword)) {
            isNewKeyword = false;
            return;
        }

        keyword = newKeyword;
        isNewKeyword = true;
    }
}
