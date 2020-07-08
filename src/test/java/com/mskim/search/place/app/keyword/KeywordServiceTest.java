package com.mskim.search.place.app.keyword;

import com.mskim.search.place.app.keyword.domain.Keyword;
import com.mskim.search.place.app.keyword.repository.KeywordRepository;
import com.mskim.search.place.app.keyword.service.KeywordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = KeywordService.class)
class KeywordServiceTest {
    @Autowired
    private KeywordService keywordService;

    @MockBean
    private KeywordRepository keywordRepository;

    private Keyword[] keywords;
    private String[] placeNames;

    @BeforeEach
    void setUp() {
        placeNames = new String[] { "서울역", "남대문" };
        keywords = new Keyword[] {
                        Keyword.builder().value(placeNames[0]).build().increaseCount(),
                        Keyword.builder().value(placeNames[1]).build()
                    };
    }

    @Test
    void KeywordService_검색어_저장() {
        // given
        given(keywordRepository.findByValue(placeNames[0])).willReturn(Optional.of(keywords[0]));
        given(keywordRepository.save(keywords[0])).willReturn(keywords[0]);

        // when
        keywordService.storeKeyword(placeNames[0]);

        // then
        verify(keywordRepository).findByValue(placeNames[0]);
        verify(keywordRepository).save(keywords[0]);
    }

    @Test
    void KeywordService_인기_검색어_조회() {
        // given
        List<Keyword> dummyKeywords = Arrays.asList(keywords);
        given(keywordRepository.findTop10ByOrderByCountDesc()).willReturn(dummyKeywords);

        // when
        dummyKeywords = keywordService.retrieveHotKeywords();
        for (Keyword keyword : dummyKeywords) {
            System.out.println(keyword);
        }

        // then
        then(dummyKeywords.size()).isEqualTo(2);
        then(dummyKeywords.get(0)).isEqualTo(keywords[0]);
        then(dummyKeywords.get(1)).isEqualTo(keywords[1]);
        then(dummyKeywords.get(0).getCount()).isEqualTo(2);
    }

}