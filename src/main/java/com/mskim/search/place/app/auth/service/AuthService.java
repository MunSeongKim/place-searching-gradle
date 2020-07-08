package com.mskim.search.place.app.auth.service;

import com.mskim.search.place.app.auth.repository.AuthRepository;
import com.mskim.search.place.app.auth.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Service
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(AuthRepository repository, PasswordEncoder passwordEncoder) {
        this.authRepository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean signUp(Member member) {
        member.changePassword(this.passwordEncoder.encode(member.getPassword()));
        return this.authRepository.save(member) != null ? true : false;
    }

    public ModelMap getModelFromSession(HttpSession session) {
        Optional<Exception> exception = Optional.ofNullable((Exception) session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));
        if (!exception.isPresent()) {
            return null;
        }

        ModelMap model = new ModelMap();

        String errorMessage = exception.get().getLocalizedMessage();
        model.addAttribute("error_message", errorMessage);

        return model;
    }
}
