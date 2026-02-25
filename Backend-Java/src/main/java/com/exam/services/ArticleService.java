package com.exam.services;

import com.exam.dto.ArticleDTO;
import com.exam.dto.ArticleRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleService {
    ArticleDTO createArticle(ArticleRequest request, Long authorId);

    ArticleDTO updateArticle(Long id, ArticleRequest request, Long authorId);

    void deleteArticle(Long id, Long authorId);

    ArticleDTO getArticleById(Long id);

    Page<ArticleDTO> getAllArticles(Pageable pageable);

    Page<ArticleDTO> searchArticles(String title, String category, Long authorId, Pageable pageable);
}
