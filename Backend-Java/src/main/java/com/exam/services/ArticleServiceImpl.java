package com.exam.services;

import com.exam.dto.ArticleDTO;
import com.exam.dto.ArticleRequest;
import com.exam.dto.TagDTO;
import com.exam.entities.Article;
import com.exam.entities.Tag;
import com.exam.entities.User;
import com.exam.entities.Role;
import com.exam.exceptions.ResourceNotFoundException;
import com.exam.repositories.ArticleRepository;
import com.exam.repositories.TagRepository;
import com.exam.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Override
    @Transactional
    public ArticleDTO createArticle(ArticleRequest request, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Set<Tag> tags = processTags(request.getTags());

        Article article = Article.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .author(author)
                .tags(tags)
                .viewCount(0L)
                .build();

        Article savedArticle = articleRepository.save(article);
        return mapToDTO(savedArticle);
    }

    @Override
    @Transactional
    public ArticleDTO updateArticle(Long id, ArticleRequest request, Long authorId) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        User user = userRepository.findById(authorId).orElseThrow();
        if (!article.getAuthor().getId().equals(authorId) && user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Not authorized to update this article");
        }

        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setCategory(request.getCategory());
        article.setTags(processTags(request.getTags()));

        Article updatedArticle = articleRepository.save(article);
        return mapToDTO(updatedArticle);
    }

    @Override
    @Transactional
    public void deleteArticle(Long id, Long authorId) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        if (authorId == null) {
            throw new RuntimeException("User must be authenticated to delete an article");
        }

        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check authorization
        boolean isAuthor = article.getAuthor() != null && article.getAuthor().getId().equals(authorId);
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isAuthor && !isAdmin) {
            throw new RuntimeException("Not authorized to delete this article");
        }

        // Remove tag associations from the join table explicitly and flush to DB
        article.getTags().clear();
        articleRepository.saveAndFlush(article);

        articleRepository.delete(article);
    }

    @Override
    @Transactional
    public ArticleDTO getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        // Increment view count
        article.setViewCount(article.getViewCount() + 1);
        articleRepository.save(article);

        return mapToDTO(article);
    }

    @Override
    public Page<ArticleDTO> getAllArticles(Pageable pageable) {
        return articleRepository.findAll(pageable).map(this::mapToDTO);
    }

    @Override
    public Page<ArticleDTO> searchArticles(String title, String category, Long authorId, Pageable pageable) {
        if (title != null && !title.isEmpty()) {
            return articleRepository.findByTitleContainingIgnoreCase(title, pageable).map(this::mapToDTO);
        }
        if (category != null && !category.isEmpty()) {
            return articleRepository.findByCategory(category, pageable).map(this::mapToDTO);
        }
        if (authorId != null) {
            return articleRepository.findByAuthorId(authorId, pageable).map(this::mapToDTO);
        }
        return getAllArticles(pageable);
    }

    private Set<Tag> processTags(Set<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        if (tagNames != null && !tagNames.isEmpty()) {
            for (String name : tagNames) {
                Tag tag = tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(name).build()));
                tags.add(tag);
            }
        }
        return tags;
    }

    private ArticleDTO mapToDTO(Article article) {
        Set<TagDTO> tagDTOs = article.getTags().stream()
                .map(tag -> new TagDTO(tag.getId(), tag.getName()))
                .collect(Collectors.toSet());

        return ArticleDTO.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .category(article.getCategory())
                .authorName(article.getAuthor().getName())
                .authorId(article.getAuthor().getId())
                .viewCount(article.getViewCount())
                .tags(tagDTOs)
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .build();
    }
}
