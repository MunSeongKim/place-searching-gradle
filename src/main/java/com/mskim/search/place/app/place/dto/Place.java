package com.mskim.search.place.app.place.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class Place {
    private int index;
    private int id;
    private String name;
    private String category;
    private String address;
    @JsonProperty("road_address")
    private String roadAddress;
    private String phone;
    @JsonProperty("shortcut_link")
    private String shortcutLink;
    private Double longitude;
    private Double latitude;

    @Builder
    public Place(int index, int id, String name, String category,
                 String address, String roadAddress, String phone,
                 String shortcutLink, Double longitude, Double latitude) {
        this.index = index;
        this.id = id;
        this.name = name;
        this.category = category;
        this.address = address;
        this.roadAddress = roadAddress;
        this.phone = phone;
        this.shortcutLink = shortcutLink;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Place assignItemIndex(int index) {
        this.index = index;

        return this;
    }
}
