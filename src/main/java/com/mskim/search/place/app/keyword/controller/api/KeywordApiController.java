package com.mskim.search.place.app.keyword.controller.api;

import com.mskim.search.place.app.keyword.domain.Keyword;
import com.mskim.search.place.app.keyword.service.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping(value = "/api/keywords", produces = "application/json;charset=UTF-8")
public class KeywordApiController {
    private final KeywordService keywordService;

    @Autowired
    public KeywordApiController(KeywordService keywordService) {
        this.keywordService = keywordService;
    }

    @GetMapping(value="/hot")
    public List<Keyword> getHotKeywords() {
        return this.keywordService.retrieveHotKeywords();
    }
    
}
