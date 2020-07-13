package com.mskim.search.place.app.place;

import com.mskim.search.place.app.place.controller.PlaceController;
import com.mskim.search.place.app.place.dto.PlaceDto;
import com.mskim.search.place.app.place.service.PlaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@ExtendWith(SpringExtension.class)
@WebMvcTest(PlaceController.class)
@AutoConfigureDataJpa
class PlaceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlaceService kakaoPlaceService;

    private String placeName;
    private int[] page;

    @BeforeEach
    void setUp() {
        placeName = "서울역";
        page = new int[] { 1, 2 };
    }

    @Test
    @WithMockUser
    void PlaceController_검색_페이지() throws Exception {
        // given
        // setUp()

        // when
        mockMvc.perform(get("/view/place").characterEncoding("UTF-8"))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("place/index"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void PlaceController_검색_실행() throws Exception {
        // given
        // setUp()
        PlaceDto placeDto = PlaceDto.builder().build();
        given(kakaoPlaceService.retrievePlace(placeName, page[0]))
                .willReturn(placeDto);

        // when
        mockMvc.perform(
                        get("/view/place/search")
                        .characterEncoding("UTF-8")
                        .queryParam("query", placeName)
                        .queryParam("page", page[0] + "")
                )
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("place/index"))
                .andExpect(model().attribute("result_place", placeDto))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void PlaceController_검색_2페이지_실행_() throws Exception {
        // given
        // setUp()
        PlaceDto placeDto = PlaceDto.builder().build();
        given(kakaoPlaceService.retrievePlace(placeName, page[1]))
                .willReturn(placeDto);

        // when
        mockMvc.perform(
                        get("/view/place/search")
                        .characterEncoding("UTF-8")
                        .queryParam("query", placeName)
                        .queryParam("page", page[1] + "")
                )
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("place/index"))
                .andExpect(model().attribute("result_place", placeDto))
                .andDo(print());
    }
}