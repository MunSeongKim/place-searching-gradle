package com.mskim.search.place.app.place.service.interfaces;

import com.mskim.search.place.app.place.dto.Place;
import com.mskim.search.place.app.place.dto.PlaceDto;
import com.sun.istack.Nullable;

import javax.servlet.http.HttpSession;

public interface PlaceSearchableStrategy {
    Place searchById(int placeId, @Nullable HttpSession session);
    PlaceDto searchByPlaceName(String placeName, @Nullable int page);
    boolean isNewKeyword();
}
