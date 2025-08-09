package com.example.info.repository;

import com.example.info.model.ResearchResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResearchResultRepository extends JpaRepository<ResearchResult, Long> {
    ResearchResult findFirstByTopicIgnoreCase(String topic);
}