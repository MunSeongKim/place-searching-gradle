package com.mskim.search.place.support;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.mskim.search.place.app.place.dto.Place;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 서울역으로 검색했을 때의 응답을 기준으로 함
 * > https://dapi.kakao.com/v2/local/search/keyword.json?query=서울역
 */
@SuppressWarnings("unchecked")
public class SampleApiResponseMaker {
    private String response;
    private ObjectMapper objectMapper;

    public SampleApiResponseMaker() throws IOException {
        File file = new File(getClass().getClassLoader()
                .getResource("support/response.json")
                .getPath());

        this.response = new String(Files.readAllBytes(file.toPath()));

        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        this.objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    }

    public ResponseEntity<Map> getResponseEntity() throws IOException {
        Map<String, Object> results = new LinkedHashMap<>();

        JsonNode node = objectMapper.readTree(response);

        ObjectReader reader = objectMapper.readerForListOf(Map.class);
        List<Map<String, Object>> documents = reader.readValue(node.get("documents"));
        results.put("documents", documents);

        reader = objectMapper.readerFor(Map.class);
        Map<String, Object> meta = reader.readValue(node.get("meta"));
        results.put("meta", meta);

        return new ResponseEntity<Map>(results, HttpStatus.OK);
    }

    public Map getResponseBody() throws IOException {
        ResponseEntity<Map> response = getResponseEntity();
        return response.getBody();
    }

    public List<Place> convertMapToDto(Map response) {
        List<Map<String, String>> placeList = (List) response.get("documents");

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
                    .shortcutLink(id+"")
                    .build();
            places.add(place);
        }

        return places;
    }

}
