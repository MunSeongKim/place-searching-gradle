package com.mskim.search.place.runner;

import com.mskim.search.place.app.auth.domain.Member;
import com.mskim.search.place.app.auth.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class UserInitializationRunner implements ApplicationRunner {
    private final Logger logger = LoggerFactory.getLogger(UserInitializationRunner.class);

    private final AuthService authService;

    @Autowired
    public UserInitializationRunner(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void run(ApplicationArguments args) {
        Member member = Member.builder()
                .account("munseong.kim")
                .password("test")
                .build();

        this.authService.signUp(member);

        logger.info("Initial member: " + member);
    }
}
