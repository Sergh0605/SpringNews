package com.exam.springnews.utils;

import com.exam.springnews.dto.ArticleDto;
import com.exam.springnews.exceptions.CustomApplicationException;
import com.exam.springnews.exceptions.CustomFileUploadException;
import com.exam.springnews.persistence.entity.article.ArticleEntity;
import com.exam.springnews.persistence.entity.article.ArticleEntityCategories;
import com.exam.springnews.service.ArticleServiceImpl;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.slf4j.LoggerFactory.getLogger;

public class ArticleUtils {
    private final static Logger log = getLogger(ArticleUtils.class);

    public static boolean isValidCategory(String category) {
        for (ArticleEntityCategories c : ArticleEntityCategories.values()) {
            if (c.name().equalsIgnoreCase(category)) return true;
        }
        return false;
    }

    public static List<ArticleDto> toDtoListConverter(List<ArticleEntity> articleEntities) {
        return articleEntities.stream().
                filter(Objects::nonNull).
                map(ArticleDto::new).
                collect(Collectors.toList());
    }

    public static Map<ZipEntry, ZipFile> zipFileValidation(ZipFile zipFile, int maxCountOfFilesInZip) {
        if (zipFile.stream().findAny().isEmpty()) {
            CustomFileUploadException e = new CustomFileUploadException("ZIP is empty.");
            log.debug(e.getMessage());
            throw e;
        }
        if (zipFile.stream().count() > maxCountOfFilesInZip) {
            CustomFileUploadException e = new CustomFileUploadException("There is more than " + maxCountOfFilesInZip + " files in the ZIP.");
            log.debug(e.getMessage());
            throw e;
        }
        if (maxCountOfFilesInZip == 1) {
            ZipEntry zipEntry = zipFile.entries().nextElement();
            if (!zipEntry.getName().equals("article.txt")) {
                CustomApplicationException e = new CustomFileUploadException("The file article.txt is missing in the ZIP.");
                log.debug(e.getMessage());
                throw e;
            }
        }
        Map<ZipEntry, ZipFile> outputMap = new HashMap<>();
        zipFile.stream().forEach(e -> {
            outputMap.put(e, zipFile);
        });
        return outputMap;
    }

    public static List<ArticleDto> readZipToArticleListWithValidation(ZipFile zipFile, int maxCountOfFilesInZip, int minCountOfLines) {
        Map<ZipEntry, ZipFile> mapOfZipEntries = zipFileValidation(zipFile, maxCountOfFilesInZip);
        return mapOfZipEntries.entrySet().stream().
                map(e -> readZipToArticle(e.getKey(), e.getValue(), minCountOfLines)).collect(Collectors.toList());
    }

    public static ArticleDto readZipToArticle(ZipEntry zipEntry, ZipFile zipFile, int minCountOfLines) throws CustomFileUploadException {
        try (BufferedReader bufferedReaderForLineCounting = new BufferedReader(new InputStreamReader(zipFile.getInputStream(zipEntry)));
             BufferedReader bufferedReaderForLineReading = new BufferedReader(new InputStreamReader(zipFile.getInputStream(zipEntry)))) {
            if (bufferedReaderForLineCounting.lines().count() < minCountOfLines) {
                CustomApplicationException e = new CustomFileUploadException("Not enough lines in the file " + zipEntry.getName());
                log.debug(e.getMessage());
                throw e;
            }
            String headLine = bufferedReaderForLineReading.readLine();
            if (headLine.isEmpty()) {
                CustomApplicationException e = new CustomFileUploadException("Headline is Empty in file " + zipEntry.getName());
                log.debug(e.getMessage());
                throw e;
            }
            String fullText = bufferedReaderForLineReading.lines().collect(Collectors.joining());
            if (fullText.isEmpty()) {
                CustomApplicationException e = new CustomFileUploadException("Article body is Empty in file " + zipEntry.getName());
                log.debug(e.getMessage());
                throw e;
            }
            return ArticleDto.builder().headline(headLine).fullText(fullText).build();
        } catch (IOException e) {
            log.debug("Exception in ArticleUtils-readZipToArticle", e);
            CustomApplicationException ex = new CustomFileUploadException("ZIP file read error.");
            log.debug(ex.getMessage());
            throw ex;
        }
    }
}
