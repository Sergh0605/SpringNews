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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${zipProperties.maxCountOfFilesInZip:1}")
    private Integer maxCountOfFilesInZip;
    @Value("${zipProperties.minCountOfLinesInArticleFile:2}")
    private Integer minCountOfLinesInArticleFile;

    public ArticleServiceImpl(ArticlesRepository articlesRepository, UserService userService) {
        this.articlesRepository = articlesRepository;
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleDto fetchById(Integer id) throws ArticleServiceException {
        Optional<ArticleEntity> article = articlesRepository.findById(id.longValue());
        if (article.isPresent()) {
            return article.map(ArticleDto::new).orElse(null);
        } else {
            throw new ArticleServiceException("Article whith Id = " + id + " not found");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleDto> fetchByCategory(String category) throws ArticleServiceException {
        if (category.equalsIgnoreCase("all")) {
            return fetchAll();
        }
        if (category.equalsIgnoreCase("fresh")) {
            return fetchNewest(10);
        }
        if (ArticleUtils.isValidCategory(category)) {
            List<ArticleEntity> articleEntities = articlesRepository.
                    findArticleEntitiesByCategory(ArticleEntityCategories.valueOf(category.toUpperCase(Locale.ROOT)), sortByPublicationDateTime);
            return ArticleUtils.toDtoListConverter(articleEntities);
        } else {
            ArticleServiceException e = new ArticleServiceException("Category " + category + " not found.");
            log.debug(e.getMessage());
            throw e;
        }
    }

    //OPTION Service should work with many files in ZIP archive. It depends on parameters in application.property
    @Override
    @Transactional
    public List<ArticleDto> createArticle(MultipartFile file,
                                          String category,
                                          Long userId,
                                          String realPathToUpload) throws CustomApplicationException {
        try {
            if (!(file.getContentType() == null) && !file.getContentType().contains("zip")) {
                CustomFileUploadException e = new CustomFileUploadException("Invalid file type.");
                log.debug(e.getMessage());
                throw e;
            }
            String fileName = "tmp" + file.getOriginalFilename();
            File tempFile = new File(realPathToUpload, fileName);
            file.transferTo(tempFile);
            ZipFile zipFile = new ZipFile(tempFile);
            List<ArticleDto> articleDtos;
            try {
                articleDtos = ArticleUtils.readZipToArticleListWithValidation(zipFile, maxCountOfFilesInZip, minCountOfLinesInArticleFile);
            } finally {
                zipFile.close();
                tempFile.delete();
            }
            UserEntity user = userService.fetchUserById(userId);
            if (!ArticleUtils.isValidCategory(category)) {
                ArticleServiceException e = new ArticleServiceException("Wrong article category.");
                log.debug(e.getMessage());
                throw e;
            }

            List<ArticleEntity> articleEntities = saveArticles(articleDtos, user, ArticleEntityCategories.valueOf(category.toUpperCase(Locale.ROOT)));
            return ArticleUtils.toDtoListConverter(articleEntities);
        } catch (CustomApplicationException e) {
            throw e;
        } catch (IOException e) {
            log.debug("Exception in ArticleServiceImpl", e);
            CustomFileUploadException ex = new CustomFileUploadException("ZIP file read error.");
            log.debug(ex.getMessage());
            throw ex;
        } catch (Exception e) {
            log.debug("Exception in ArticleServiceImpl", e);
            CustomFileUploadException ex = new CustomFileUploadException("Can't parse or save uploaded file.");
            log.debug(ex.getMessage());
            throw ex;
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
    @Transactional(readOnly = true)
    public List<ArticleDto> fetchAll() {
        List<ArticleEntity> articleEntities = articlesRepository.findAll(sortByPublicationDateTime);
        return ArticleUtils.toDtoListConverter(articleEntities);
    }

    @Override
    @Transactional(readOnly = true)
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
