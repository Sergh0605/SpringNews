package com.exam.springnews.service;

import com.exam.springnews.dto.ArticleDto;
import com.exam.springnews.exceptions.ArticleServiceException;
import com.exam.springnews.exceptions.CustomApplicationException;
import com.exam.springnews.exceptions.CustomFileUploadException;
import com.exam.springnews.persistence.ArticlesRepository;
import com.exam.springnews.persistence.entity.article.ArticleEntity;
import com.exam.springnews.persistence.entity.article.ArticleEntityCategories;
import com.exam.springnews.persistence.entity.user.UserEntity;
import com.exam.springnews.utils.ArticleUtils;
import org.slf4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class ArticleServiceImpl implements ArticlesService {
    private final static Logger log = getLogger(ArticleServiceImpl.class);
    private final Sort sortByPublicationDateTime = Sort.by(Sort.Direction.DESC, "publicationDateTime");
    private final ArticlesRepository articlesRepository;
    private final UserService userService;

    public ArticleServiceImpl(ArticlesRepository articlesRepository, UserService userService) {
        this.articlesRepository = articlesRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public ArticleDto fetchById(Integer id) throws ArticleServiceException{
        Optional<ArticleEntity> article = articlesRepository.findById(id.longValue());
        if (article.isPresent()) {
            return article.map(ArticleDto::new).orElse(null);
        } else {throw new ArticleServiceException("Article whith Id = " + id + " not found");}
    }

    @Override
    @Transactional
    public List<ArticleDto> fetchByCategory(String category) throws ArticleServiceException {
        if (category.equalsIgnoreCase("all")) {
            return fetchAll();
        }
        if (category.equalsIgnoreCase("fresh")) {
            return fetchNewest(10);
        }
        if (ArticleUtils.isValidCategory(category)) {
            List<ArticleEntity> articleEntities = articlesRepository.
                    findArticleEntitiesByCategory(ArticleEntityCategories.valueOf(category), sortByPublicationDateTime);
            return ArticleUtils.toDtoListConverter(articleEntities);
        }
        throw new ArticleServiceException("Category " + category + " not found.");
    }

    @Override
    @Transactional
    public List<ArticleDto> createArticle(MultipartFile file,
                                          String category,
                                          Long userId,
                                          Integer maxCountOfFilesInZip,
                                          Integer minCountOfLinesInArticle,
                                          String realPathToUpload) throws CustomApplicationException {
        try {
            if (!(file.getContentType() == null) && !file.getContentType().contains("zip")) {
                throw new CustomFileUploadException("Invalid file type.");
            }
            String fileName = "tmp" + file.getOriginalFilename();
            File tempFile = new File(realPathToUpload, fileName);
            file.transferTo(tempFile);
            ZipFile zipFile = new ZipFile(tempFile);
            List<ArticleDto> articleDtos;
            try {
                articleDtos = ArticleUtils.readZipToArticleListWithValidation(zipFile, maxCountOfFilesInZip, minCountOfLinesInArticle);
            } finally {
                zipFile.close();
                tempFile.delete();
            }
            UserEntity user = userService.fetchUserById(userId);
            if (!ArticleUtils.isValidCategory(category))
                throw new ArticleServiceException("Wrong article category.");
            List<ArticleEntity> articleEntities = saveArticles(articleDtos, user, ArticleEntityCategories.valueOf(category));
            return ArticleUtils.toDtoListConverter(articleEntities);
        } catch (IOException ex) {
            log.debug(ex.getMessage());
            throw new CustomFileUploadException("ZIP file read error.");
        }
    }

    private List<ArticleEntity> saveArticles(List<ArticleDto> articles, UserEntity user, ArticleEntityCategories
            category) {
        List<ArticleEntity> articleEntityList = new ArrayList<>();
        for (ArticleDto articleDto : articles) {
            ArticleEntity article = ArticleEntity.builder().
                    category(category).
                    headline(articleDto.getHeadline()).
                    text(articleDto.getFullText()).
                    publicationDateTime(new Date()).
                    user(user).build();
            article = articlesRepository.save(article);
            articleEntityList.add(article);
        }
        return articleEntityList;
    }

    @Override
    @Transactional
    public List<ArticleDto> fetchAll() {
        List<ArticleEntity> articleEntities = articlesRepository.findAll(sortByPublicationDateTime);
        return ArticleUtils.toDtoListConverter(articleEntities);
    }

    @Override
    @Transactional
    public List<ArticleDto> fetchNewest(Integer count) {
        List<ArticleEntity> articleEntities = articlesRepository.
                findAll(PageRequest.of(0, count, sortByPublicationDateTime)).getContent();
        return ArticleUtils.toDtoListConverter(articleEntities);
    }

    @Override
    public List<String> fetchAllCategories() {
        return Arrays.stream(ArticleEntityCategories.values()).map(Enum::toString).sorted().collect(Collectors.toList());
    }
}
