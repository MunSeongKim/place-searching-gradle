package com.mskim.search.place.app.auth;

import com.mskim.search.place.app.auth.controller.AuthController;
import com.mskim.search.place.app.auth.domain.Member;
import com.mskim.search.place.app.auth.repository.AuthRepository;
import com.mskim.search.place.app.auth.service.AuthService;
import com.mskim.search.place.app.place.controller.PlaceController;
import com.mskim.search.place.app.place.service.PlaceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest({AuthController.class, PlaceController.class})
class SecurityConfigureTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;
    @MockBean
    private AuthRepository authRepository;
    @MockBean
    private PlaceService placeService;

    @Test
    void SecurityConfigure_로그인_페이지로_리다이렉션() throws Exception {
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
    void SecurityConfigure_로그인_성공() throws Exception {
        // given
        final String account = "successAccount";
        final String password = "temp";
        given(authRepository.findByAccount(account))
                .willReturn(
                        Optional.of(Member.builder()
                                .account(account)
                                .password(new BCryptPasswordEncoder().encode(password))
                                .build())
                );

        // when
        mockMvc.perform(
                formLogin("/auth/validation")
                        .user("id", account)
                        .password("password", password)
                )
                // then
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated())
                .andDo(print());
    }

    @Test
    void SecurityConfigure_로그인_실패() throws Exception {
        // given
        final String account = "failAccount";
        final String password = "temp";

        // when
        mockMvc.perform(formLogin("/auth/validation")
                            .user("id", account)
                            .password("password", password))
                // then
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/view/auth/sign_in?error=true"))
                .andDo(print());
    }



    @Test
    void SecurityConfigure_로그아웃() throws Exception {
        // given
        // null

        // when
        mockMvc.perform(logout("/auth/sign_out"))
                // then
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/view/auth/sign_in"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void SecurityConfigure_인증된_후_페이지_접속() throws Exception {
        // given
        // null

        // when
        mockMvc.perform(get("/"))
                // then
                .andExpect(status().isOk())
                .andDo(print());
    }
}