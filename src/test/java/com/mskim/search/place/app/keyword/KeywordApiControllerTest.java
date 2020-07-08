package com.mskim.search.place.app.keyword;

import com.mskim.search.place.app.keyword.controller.api.KeywordApiController;
import com.mskim.search.place.app.keyword.domain.Keyword;
import com.mskim.search.place.app.keyword.service.KeywordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(KeywordApiController.class)
@AutoConfigureJsonTesters
@AutoConfigureDataJpa
class KeywordApiControllerTest {
    @Autowired
    private JacksonTester<List<Keyword>> json;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KeywordService keywordService;

    @Test
    @WithMockUser
    void KeywordApiController_인기_키워드_조회() throws Exception {
        // given
        List<Keyword> sampleKeywords = new ArrayList<>();
        for (int i = 20; i >= 1; i--) {
            Keyword keyword = Keyword.builder().value("키워드" + i).build();
            for (int j = 1; j < i; j++) {
                keyword.increaseCount();
            }
            sampleKeywords.add(keyword);
        }

        given(keywordService.retrieveHotKeywords()).willReturn(sampleKeywords.subList(0, 10));

        // when
        MvcResult result = mockMvc.perform(get("/api/keywords/hot").characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        // then
        then(result.getResponse().getContentAsString()).isNotNull();

        List<Keyword> keywords = json.parseObject(result.getResponse().getContentAsString());
        for (Keyword item : keywords) {
            System.out.println(item.toString());
        }

        then(keywords).isNotNull();
        then(keywords.size()).isEqualTo(10);
        then(keywords.get(0).getValue()).isEqualTo("키워드20");
        then(keywords.get(0).getCount()).isEqualTo(20);
    }


}