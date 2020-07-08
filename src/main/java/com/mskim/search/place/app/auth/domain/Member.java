package com.mskim.search.place.app.auth.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "t_member")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;
    @Column(length = 50, nullable = false, unique = true, updatable = false)
    private String account;
    @Column(nullable = false)
    private String password;

    @Builder
    public Member(String account, String password) {
        this.account = account;
        this.password = password;
    }

    public Member changePassword(String newPassword) {
        this.password = newPassword;

        return this;
    }
}
