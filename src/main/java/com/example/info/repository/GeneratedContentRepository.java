package com.example.info.repository;

import com.example.info.model.GeneratedContent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GeneratedContentRepository extends JpaRepository<GeneratedContent, Long> {
    List<GeneratedContent> findByTopicIgnoreCaseOrderByCreatedAtDesc(String topic);
    List<GeneratedContent> findByStatusOrderByCreatedAtDesc(String status);
}