package com.mskim.search.place.app.auth;

import com.mskim.search.place.app.auth.domain.Member;
import com.mskim.search.place.app.auth.repository.AuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.BDDAssertions.then;


@DataJpaTest
class AuthRepositoryTest {
    @Autowired
    private AuthRepository authRepository;

    private Member member;
    private String account;
    private String password;


    @BeforeEach
    void setUp() {
        account = "testAccount";
        password = "testPassword";

        member = Member.builder()
                    .account(account)
                    .password(password)
                    .build();
    }

    @Test
    void AuthRepository_사용자_등록() {
        // given
        // setUp();

        // when
        final Member savedMember = this.authRepository.save(member);

        // then
        then(savedMember).isNotNull();
        then(savedMember.getMemberId()).isNotZero().isGreaterThan(0);
        then(savedMember.getAccount()).isEqualTo(account);
        then(savedMember.getPassword()).isEqualTo(password);
    }

    @Test
    void AuthRepository_사용자_조회() {
        // given
        // setUp();
        this.authRepository.save(member);

        // when
        final Member retrievedMember = this.authRepository.findByAccount(member.getAccount()).orElse(null);

        // then
        then(retrievedMember).isNotNull();
        then(retrievedMember.getMemberId()).isNotZero().isGreaterThan(0);
        then(retrievedMember.getAccount()).isNotBlank().isEqualTo(account);
        then(retrievedMember.getPassword()).isNotBlank().isEqualTo(password);
    }

    @Test
    void AuthRepository_존재하지_않는_사용자_조회() {
        // given
        // setUp();

        // when
        final Member retrievedMember = this.authRepository.findByAccount("NotExistAccount").orElse(null);

        // then
        then(retrievedMember).isNull();
    }
}