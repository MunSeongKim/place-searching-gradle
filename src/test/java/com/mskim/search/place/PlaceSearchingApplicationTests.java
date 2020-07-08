package com.mskim.search.place;

import com.mskim.search.place.app.keyword.domain.Keyword;
import com.mskim.search.place.app.keyword.service.KeywordService;
import com.mskim.search.place.app.place.dto.Place;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Integration Test
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PlaceSearchingApplicationTests {
    @Autowired
    private MockMvc mockMvc;
	@Autowired
	private JacksonTester<Place> placeJson;
	@Autowired
	private JacksonTester<List<Keyword>> keywordsJson;
	@Autowired
	private KeywordService keywordService;

	@Test
	@Order(1)
	void 서비스_접속_미인증_리다리엑션() throws Exception {
		// given
		// null

		// when
		mockMvc.perform(get("/"))
				// then
				.andExpect(status().isFound())
				.andExpect(redirectedUrlPattern("**/view/auth/sign_in"))
				.andDo(print());
	}

	@Test
	@Order(2)
	void 로그인() throws Exception {
		// given
		// null

		// when
		mockMvc.perform(formLogin("/auth/validation")
							.user("id", "munseong.kim")
							.password("password", "test"))
				// then
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/"))
				.andExpect(authenticated())
				.andDo(print());
	}

	@Test
	@WithMockUser
	@Order(3)
	void 장소_검색_페이지_요청() throws Exception {
		// given
		// null

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
	@Order(4)
	void 장소_검색_실행() throws Exception {
		// given
		// null

		// when
		mockMvc.perform(
						get("/view/place/search")
						.characterEncoding("UTF-8")
						.queryParam("query", "서울역")
						.queryParam("page", "1")
				)
		// then
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
				.andExpect(view().name("place/index"))
				.andExpect(model().attributeExists("result_place"))
				.andDo(print())
				.andReturn();
	}

	@Test
	@WithMockUser
	@Order(5)
	void 장소_상세_조회() throws Exception {
		// given
		// null

		// when
		MvcResult result = mockMvc.perform(
						get("/api/places/9113903")
						.characterEncoding("UTF-8")
				)
		//then
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andReturn();

		then(result.getResponse().getContentAsString()).isNotNull();

		Place place = placeJson.parseObject(result.getResponse().getContentAsString());
		then(place).isNotNull();
		then(place.getId()).isEqualTo(9113903);
		then(place.getName()).isEqualTo("서울역");
		then(place.getShortcutLink()).isNotBlank();
	}

	@Test
	@WithMockUser
	@Order(6)
	void 인기_키워드_조회() throws Exception {
		// given
		keywordService.storeKeyword("서울역");

		// when
		MvcResult result = mockMvc.perform(
						get("/api/keywords/hot")
						.characterEncoding("UTF-8")
				)
		// then
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andReturn();

		// then
		then(result.getResponse().getContentAsString()).isNotNull();

		List<Keyword> keywords = keywordsJson.parseObject(result.getResponse().getContentAsString());
		then(keywords).isNotNull();
		then(keywords.get(0).getValue()).isEqualTo("서울역");
		then(keywords.get(0).getCount()).isGreaterThanOrEqualTo(1);
	}

	@Test
	@WithMockUser
	@Order(7)
	void 장소_결과_페이지_이동() throws Exception {
		// given
		// null

		// when
		mockMvc.perform(
						get("/view/place/search")
						.characterEncoding("UTF-8")
						.queryParam("query", "서울역")
						.queryParam("page", "2")
				)
		// then
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
				.andExpect(view().name("place/index"))
				.andExpect(model().attributeExists("result_place"))
				.andDo(print())
				.andReturn();
	}

	@Test
	@Order(8)
	void 로그아웃() throws Exception {
		// given
		// null

		// when
		mockMvc.perform(logout("/auth/sign_out"))
		// then
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/view/auth/sign_in"))
				.andExpect(unauthenticated())
				.andDo(print());
	}

}
