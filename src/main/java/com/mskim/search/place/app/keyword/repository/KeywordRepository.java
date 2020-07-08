package com.mskim.search.place.app.keyword.repository;

import com.mskim.search.place.app.keyword.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    Optional<Keyword> findByValue(String keyword);
    List<Keyword> findTop10ByOrderByCountDesc();
}
