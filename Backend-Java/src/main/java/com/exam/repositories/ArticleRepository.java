package com.exam.repositories;

import com.exam.entities.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findByCategory(String category, Pageable pageable);

    Page<Article> findByAuthorId(Long authorId, Pageable pageable);

    Page<Article> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
