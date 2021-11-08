package com.exam.springnews.persistence;

import com.exam.springnews.persistence.entity.article.ArticleEntity;
import com.exam.springnews.persistence.entity.article.ArticleEntityCategories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticlesRepository extends CrudRepository<ArticleEntity, Long> {
    List<ArticleEntity> findArticleEntitiesByCategory(ArticleEntityCategories category, Sort sort);

    List<ArticleEntity> findAll(Sort sort);

    Page<ArticleEntity> findAll(Pageable pageable);

}
