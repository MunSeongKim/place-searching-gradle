package com.mskim.search.place.app.auth;

import com.mskim.search.place.app.auth.controller.AuthController;
import com.mskim.search.place.app.auth.repository.AuthRepository;
import com.mskim.search.place.app.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.ModelMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private AuthRepository authRepository;

    @Test
    void AuthController_로그인_페이지() throws Exception {
        // given
        // null

        // when
        mockMvc.perform(get("/view/auth/sign_in"))
                // then
                .andExpect(status().isOk())
                .andExpect(view().name("auth/sign_in"))
                .andExpect(model().attributeDoesNotExist("error_message"))
                .andDo(print());
    }

    @Test
    void AuthController_에러시_로그인_페이지_() throws Exception {
        // given
        String modelKey = "error_message";
        String errorMessage = "ID/Password invalid.";

        given(authService.getModelFromSession(any()))
                .willReturn(new ModelMap(modelKey, errorMessage));

        // when
        mockMvc.perform(get("/view/auth/sign_in?error=true"))
                // then
                .andExpect(status().isOk())
                .andExpect(view().name("auth/sign_in"))
                .andExpect(model().attribute(modelKey, errorMessage))
                .andReturn();
    }
}