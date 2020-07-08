package com.mskim.search.place.configuration.security.support;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SignInFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private static final String FAILURE_PARAMETER_KEY = "error";
    private static final String FAILURE_PARAMETER_VALUE = "true";

    private RequestCache requestCache = new HttpSessionRequestCache();

    public SignInFailureHandler(String defaultFailureUrl) {
        super.setDefaultFailureUrl(this.generateFailureRedirectUrl(defaultFailureUrl));
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        super.onAuthenticationFailure(request, response, exception);
    }

    private String generateFailureRedirectUrl(String failureUrl) {
        return failureUrl + "?" + FAILURE_PARAMETER_KEY + "=" + FAILURE_PARAMETER_VALUE;
    }
}
