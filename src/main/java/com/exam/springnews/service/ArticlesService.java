package com.exam.springnews.service;

import com.exam.springnews.dto.ArticleDto;
import com.exam.springnews.exceptions.ArticleServiceException;
import com.exam.springnews.exceptions.CustomApplicationException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArticlesService {
    ArticleDto fetchById(Integer id) throws ArticleServiceException;

    List<ArticleDto> fetchByCategory(String category) throws ArticleServiceException;

    List<ArticleDto> createArticle(MultipartFile file,
                                   String category,
                                   Long userId,
                                   String realPathToUpload) throws CustomApplicationException;

    List<ArticleDto> fetchAll();

    List<ArticleDto> fetchNewest(Integer count);

    List<String> fetchAllCategories();
}
