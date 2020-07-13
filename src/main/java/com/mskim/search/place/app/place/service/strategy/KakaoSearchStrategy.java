package com.mskim.search.place.app.place.service.strategy;

import com.mskim.search.place.app.place.dto.Place;
import com.mskim.search.place.app.place.dto.PlaceDto;
import com.mskim.search.place.app.place.dto.page.PlacePager;
import com.mskim.search.place.app.place.service.interfaces.PlaceSearchableStrategy;
import com.mskim.search.place.app.place.support.client.KakaoMapSearchRestClient;
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
public class KakaoSearchStrategy implements PlaceSearchableStrategy {
    public static final String PLACE_CACHE_NAME = "place";
    public static final String PLACES_CACHE_NAME = "places";
    private static final String SHORTCUT_URL_PREFIX = "https://map.kakao.com/link/map/";

    private final CacheManager cacheManager;
    private final KakaoMapSearchRestClient client;

    private boolean isNewKeyword;
    private String keyword;
    private PlacePager placePager;

    @Autowired
    public KakaoSearchStrategy(CacheManager cacheManager, KakaoMapSearchRestClient client) {
        this.cacheManager = cacheManager;
        this.client = client;
    }

    @Override
    public Place searchById(int placeId, HttpSession session) {
        Place place = cacheManager.getCache(PLACE_CACHE_NAME)
                .get(placeId, Place.class);

        if (session != null && place == null) {
            String keyword = session.getAttribute("keyword").toString();
            int page = Integer.parseInt(session.getAttribute("page").toString());
            List cacheData = cacheManager.getCache(PLACES_CACHE_NAME)
                    .get(keyword + "_" + page, List.class);
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

        List<Place> places = getPlacesFromApi(placeName, page);

        if (places.isEmpty()) {
            return PlaceDto.builder()
                    .places(places)
                    .pager(null)
                    .build();
        }

        this.placePager = updatePager(placeName, page);
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

    private List<Place> getPlacesFromApi(String placeName, int page) {
        String cacheItemKey = getPleacesCacheKey(placeName, page);
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

        MultiValueMap<String, String> params = createParams(placeName, page);
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
                    .shortcutLink(SHORTCUT_URL_PREFIX + id)
                    .build();
            cacheManager.getCache(PLACE_CACHE_NAME).put(id, place);
            places.add(place);
        }

        return places;
    }

    private MultiValueMap<String, String> createParams(String placeName, Integer page) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("query", placeName);
        params.add("page", page.toString());

        return params;
    }

    private String getPleacesCacheKey(String keyword, int page) {
        return keyword + "_" + page;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseMetadata(Map response) {
        return (Map<String, Object>) response.get("meta");
    }

    private PlacePager updatePager(String keyword, int page) {
        if (isNewKeyword) {
            return initializePager(keyword);
        }

        return this.placePager.update(page);
    }

    private PlacePager initializePager(String keyword) {
        MultiValueMap<String, String> params = createParams(keyword, PlacePager.MAX_PAGE_COUNT);

        // new pager
        @SuppressWarnings("unchecked")
        Map<String, Object> response = (Map) client.setParams(params).getListAsEntity();
        Map<String, Object> meta = parseMetadata(response);

        Integer totalItemCount = (Integer) meta.get("pageable_count");

        return PlacePager.builder()
                .totalItemCount(totalItemCount)
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
