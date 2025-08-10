package com.example.info.repository;

import com.example.info.model.ResearchQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResearchQueryRepository extends JpaRepository<ResearchQuery, Long> {
    ResearchQuery findFirstByTopicIgnoreCase(String topic);
}
