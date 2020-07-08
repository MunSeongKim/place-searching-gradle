package com.mskim.search.place.app.keyword;

import com.mskim.search.place.app.keyword.domain.Keyword;
import com.mskim.search.place.app.keyword.repository.KeywordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

@DataJpaTest
@Transactional
class KeywordRepositoryTest {
    @Autowired
    private KeywordRepository keywordRepository;

    private Keyword keyword;
    private String placeName;

    @BeforeEach
    void setUp() {
        placeName = "키워드";

        for (int i = 1; i <= 20; i++) {
            keyword = Keyword.builder().value(placeName + i).build();
            for (int j = 1; j < i; j++) {
                keyword.increaseCount();
            }
            this.keywordRepository.save(keyword);
        }
    }

    @Test
    void KeywordRepository_검색어_저장_조회_확인() {
        // given
        // setUp();

        // when
        final Keyword keyword7 = this.keywordRepository.findByValue(placeName + "7").orElse(null);
        final Keyword keyword10 = this.keywordRepository.findByValue(placeName + "10").orElse(null);
        System.out.println(keyword7);
        System.out.println(keyword10);

        // then
        then(keyword7).isNotNull();
        then(keyword7.getValue()).isEqualTo(placeName + 7);
        then(keyword7.getCount()).isEqualTo(7);

        then(keyword10).isNotNull();
        then(keyword10.getValue()).isEqualTo(placeName + 10);
        then(keyword10.getCount()).isEqualTo(10);
    }

    @Test
    void KeywordRepository_검색어_TOP_10_조회() {
        // given
        // setUp();

        // when
        List<Keyword> hotKeywords = this.keywordRepository.findTop10ByOrderByCountDesc();
        for (Keyword item : hotKeywords) {
            System.out.println(item.toString());
        }

        // then
        then(hotKeywords).isNotNull();
        then(hotKeywords.size()).isEqualTo(10);
        then(hotKeywords.get(0).getValue()).isEqualTo(placeName + 20);
        then(hotKeywords.get(0).getCount()).isEqualTo(20);
    }
}