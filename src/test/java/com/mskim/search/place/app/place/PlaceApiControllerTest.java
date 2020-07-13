package com.mskim.search.place.app.place;

import com.mskim.search.place.app.place.controller.api.PlaceApiController;
import com.mskim.search.place.app.place.dto.Place;
import com.mskim.search.place.app.place.service.PlaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PlaceApiController.class)
@AutoConfigureJsonTesters
@AutoConfigureDataJpa
class PlaceApiControllerTest {
    @Autowired
    private JacksonTester<Place> json;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlaceService kakaoPlaceService;
    @Mock
    private MockHttpSession httpSession;

    private String[] placeNames;
    private int page = 1;
    private int placeId = 9113903;

    @BeforeEach
    void setUp() {
        placeNames = new String[] { "서울역", "남대문" };
        page = 1;
        placeId = 9113903;
    }

    @Test
    @WithMockUser
    void PlaceApiController_장소_상세_조회() throws Exception {
        // given
        given(kakaoPlaceService.retrievePlaceDetail(eq(placeId), any()))
                .willReturn(Place.builder().build());

        // when
        mockMvc.perform(
                        get("/api/places/" + placeId)
                        .characterEncoding("UTF-8")
                )
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }
}