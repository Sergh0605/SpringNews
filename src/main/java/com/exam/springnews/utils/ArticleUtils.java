package com.exam.springnews.utils;

import com.exam.springnews.dto.ArticleDto;
import com.exam.springnews.exceptions.CustomApplicationException;
import com.exam.springnews.exceptions.CustomFileUploadException;
import com.exam.springnews.persistence.entity.article.ArticleEntity;
import com.exam.springnews.persistence.entity.article.ArticleEntityCategories;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ArticleUtils {
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

    public static Map<ZipEntry, ZipFile> zipFileValidation(ZipFile zipFile, int maxCountOfFilesInZip) throws CustomApplicationException {
        if (zipFile.stream().findAny().isEmpty()) throw new CustomFileUploadException("ZIP is empty.");
        if (zipFile.stream().count() > maxCountOfFilesInZip)
            throw new CustomFileUploadException("There is more than " + maxCountOfFilesInZip + " files in the ZIP.");
        ZipEntry zipEntry = zipFile.entries().nextElement();
        if (!zipEntry.getName().equals("article.txt"))
            throw new CustomFileUploadException("The file article.txt is missing in the ZIP.");
        Map<ZipEntry, ZipFile> outputMap = new HashMap<>();
        outputMap.put(zipEntry, zipFile);
        return outputMap;
    }

    public static List<ArticleDto> readZipToArticleListWithValidation(ZipFile zipFile, int maxCountOfFilesInZip, int minCountOfLines) throws CustomApplicationException {
        Map<ZipEntry, ZipFile> mapOfZipEntries = zipFileValidation(zipFile, maxCountOfFilesInZip);
        return mapOfZipEntries.entrySet().stream().
                map(e -> readZipToArticle(e.getKey(), e.getValue(), minCountOfLines)).collect(Collectors.toList());
    }

    public static ArticleDto readZipToArticle(ZipEntry zipEntry, ZipFile zipFile, int minCountOfLines) throws CustomFileUploadException {
        try (BufferedReader bufferedReaderForLineCounting = new BufferedReader(new InputStreamReader(zipFile.getInputStream(zipEntry)));
             BufferedReader bufferedReaderForLineReading = new BufferedReader(new InputStreamReader(zipFile.getInputStream(zipEntry)))) {
            if (bufferedReaderForLineCounting.lines().count() <= minCountOfLines)
                throw new CustomFileUploadException("Not enough lines in the file " + zipEntry.getName());
            String headLine = bufferedReaderForLineReading.readLine();
            String fullText = bufferedReaderForLineReading.lines().collect(Collectors.joining());
            return ArticleDto.builder().headline(headLine).fullText(fullText).build();
        } catch (IOException e) {
            throw new CustomFileUploadException("ZIP file read error.");
        }
    }
}
