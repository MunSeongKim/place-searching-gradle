package com.mskim.search.place.app.keyword.domain;

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
@Table(name = "t_search_keyword")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "keyword_id")
    private Long id;
    @Column(nullable = false, unique = true, updatable = false)
    private String value;
    @Column(nullable = false)
    private Integer count;

    @Builder
    public Keyword(String value) {
        this.value = value;
        this.count = 1;
    }

    public Keyword increaseCount() {
        this.count = this.count + 1;

        return this;
    }
}
