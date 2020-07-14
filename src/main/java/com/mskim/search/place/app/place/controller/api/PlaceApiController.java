package com.mskim.search.place.app.place.controller.api;

import com.mskim.search.place.app.place.dto.Place;
import com.mskim.search.place.app.place.service.PlaceService;
import com.mskim.search.place.app.place.service.interfaces.PlaceSearchableStrategy;
import com.mskim.search.place.app.place.service.strategy.support.PlaceSearchStrategyConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping(value = "/api/places", produces = "application/json;charset=UTF-8")
public class PlaceApiController {
    private final PlaceService placeService;

    @Autowired
    public PlaceApiController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public Place getPlaceById(@PathVariable("id") int placeId, HttpServletRequest request) {
        this.placeService.applyStrategy(PlaceSearchStrategyConstant.KAKAO);
        return this.placeService.retrievePlaceDetail(placeId, request.getSession());
    }
}
