package com.mskim.search.place.app.place.controller;

import com.mskim.search.place.app.place.service.PlaceService;
import com.mskim.search.place.app.place.service.interfaces.PlaceSearchableStrategy;
import com.mskim.search.place.app.place.service.strategy.support.PlaceSearchStrategyConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@PreAuthorize("isAuthenticated()")
@RequestMapping({"", "/", "/view/place"})
public class PlaceController {
    private final PlaceService placeService;

    @Autowired
    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping("")
    public String index() {
        return "place/index";
    }

    @GetMapping("/search")
    public ModelAndView search(@RequestParam(name = "query") String placeName,
                               @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                               @RequestParam(name = "engine", required = false, defaultValue = "kakao") String strategy,
                               HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.setAttribute("keyword", placeName);
        session.setAttribute("page", page);

        this.placeService.applyStrategy(PlaceSearchStrategyConstant.valueOfName(strategy));

        return new ModelAndView("place/index",
                "result_place",
                this.placeService.retrievePlace(placeName, page));
    }
}
