package com.mskim.search.place.app.place.dto;

import com.mskim.search.place.app.place.dto.page.PlacePager;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class PlaceDto {
    private List<Place> places;
    private PlacePager pager;

    @Builder
    public PlaceDto(List<Place> places, PlacePager pager) {
        this.places = places;
        this.pager = pager;
    }
}
