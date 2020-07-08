package com.mskim.search.place.app.auth;

import com.mskim.search.place.app.auth.domain.Member;
import com.mskim.search.place.app.auth.repository.AuthRepository;
import com.mskim.search.place.app.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpSession;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = AuthService.class)
class AuthServiceTest{
    @Autowired
    private AuthService authService;

    @MockBean
    private AuthRepository authRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void AuthService_회원_가입_등록() {
        // given
        final Member member = Member.builder()
                .account("service_test1")
                .password("passwd")
                .build();
        given(authRepository.save(member)).willReturn(member);

        // when
        boolean result = this.authService.signUp(member);

        // then
        then(result).isTrue();
    }

    @Test
    void AuthService_에러가_없는_경우_모델_처리() {
        // given
        HttpSession session = new MockHttpSession();

        // when
        ModelMap modelMap = this.authService.getModelFromSession(session);

        // then
        then(modelMap).isNull();
    }

    @Test
    void AuthService_에러가_있는_경우_모델_처리() {
        // given
        String errorMessage = "Bad credentials";
        HttpSession session = new MockHttpSession();
        session.setAttribute("SPRING_SECURITY_LAST_EXCEPTION", new BadCredentialsException(errorMessage));

        // when
        ModelMap modelMap = this.authService.getModelFromSession(session);

        // then
        then(modelMap).isNotNull()
                .containsKey("error_message")
                .containsValue(errorMessage);
    }
}