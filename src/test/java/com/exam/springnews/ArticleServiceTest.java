package com.exam.springnews;

import com.exam.springnews.dto.ArticleDto;
import com.exam.springnews.exceptions.CustomFileUploadException;
import com.exam.springnews.persistence.ArticlesRepository;
import com.exam.springnews.persistence.entity.article.ArticleEntity;
import com.exam.springnews.persistence.entity.article.ArticleEntityCategories;
import com.exam.springnews.persistence.entity.user.UserEntity;
import com.exam.springnews.service.ArticleServiceImpl;
import com.exam.springnews.service.ArticlesService;
import com.exam.springnews.service.UserService;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArticleServiceTest {

    @Mock
    private ArticlesRepository articlesRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ArticleServiceImpl articlesService;

    @Test
    public void testCreateArticleWithEmptyZip() throws IOException, IllegalAccessException {
        String expectedExceptionMessage = "ZIP is empty.";
        String fileName = "emptyZip.zip";
        Exception exception = getExceptionWithSingleFileInZip(fileName, "zip", "culture");
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedExceptionMessage));
    }

    @Test
    public void testCreateArticleWithOneBrokenNameArticle() throws IOException, IllegalAccessException {
        String expectedExceptionMessage = "The file article.txt is missing in the ZIP.";
        String fileName = "oneBrokenNameValidArticle.zip";
        Exception exception = getExceptionWithSingleFileInZip(fileName, "zip", "culture");
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedExceptionMessage));
    }

    @Test
    public void testCreateArticleWithOneValidNameEmptyArticle() throws IOException, IllegalAccessException {
        String expectedExceptionMessage = "Not enough lines in the file article.txt";
        String fileName = "oneValidNameBrokenArticle.zip";
        Exception exception = getExceptionWithSingleFileInZip(fileName, "zip", "culture");
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedExceptionMessage));
    }

    @Test
    public void testCreateArticleWithOneValidNameEmptyLinesArticle() throws IOException, IllegalAccessException {
        String expectedExceptionMessage = "Headline is Empty in file article.txt";
        String fileName = "oneValidNameEmptyLinesArticle.zip";
        Exception exception = getExceptionWithSingleFileInZip(fileName, "zip", "culture");
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedExceptionMessage));
    }

    @Test
    public void testCreateArticleWithOneValidNameEmptyBodyArticle() throws IOException, IllegalAccessException {
        String expectedExceptionMessage = "Article body is Empty in file article.txt";
        String fileName = "oneValidNameEmptyBodyArticle.zip";
        Exception exception = getExceptionWithSingleFileInZip(fileName, "zip", "culture");
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedExceptionMessage));
    }

    @Test
    public void testCreateArticleWithBrokenTypeOfZip() throws IOException, IllegalAccessException {
        String expectedExceptionMessage = "Invalid file type.";
        String fileName = "oneValidNameValidArticle.zip";
        Exception exception = getExceptionWithSingleFileInZip(fileName, "rar", "culture");
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedExceptionMessage));
    }

    @Test
    public void testCreateValidArticleWithInvalidCategory() throws IOException, IllegalAccessException {
        String expectedExceptionMessage = "Wrong article category.";
        String fileName = "oneValidNameValidArticle.zip";
        Exception exception = getExceptionWithSingleFileInZip(fileName, "zip", "InvalidCategory");
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedExceptionMessage));
    }

    @Test
    public void testCreateArticleWithManyFilesInZip() throws IOException, IllegalAccessException {
        String expectedExceptionMessage = "There is more than 1 files in the ZIP.";
        String fileName = "twoValidArticles.zip";
        Exception exception = getExceptionWithSingleFileInZip(fileName, "zip", "culture");
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedExceptionMessage));
    }

    @Test
    public void testCreateArticleWithOneValidFileInZip() throws IOException, IllegalAccessException {
        int expectedCountOfDtoInList = 1;
        int maxCountOfFilesInZip = 1;
        String fileName = "oneValidNameValidArticle.zip";
        List<ArticleDto> articles = getArticlesListFromService(fileName, maxCountOfFilesInZip);
        assertEquals(expectedCountOfDtoInList, articles.size());
    }

    //OPTION Service should work with many files in ZIP archive. It depends on parameters in application.property
    @Test
    public void testCreateArticleWithTwoValidFileInZip() throws IOException, IllegalAccessException {
        int expectedCountOfDtoInList = 2;
        int maxCountOfFilesInZip = 3;
        String fileName = "twoValidArticles.zip";
        List<ArticleDto> articles = getArticlesListFromService(fileName, maxCountOfFilesInZip);
        assertEquals(expectedCountOfDtoInList, articles.size());
    }

    private Exception getExceptionWithSingleFileInZip(String fileName, String fileType, String category) throws IOException, IllegalAccessException {
        FieldUtils.writeField(articlesService, "maxCountOfFilesInZip", 1, true);
        FieldUtils.writeField(articlesService, "minCountOfLinesInArticleFile", 2, true);
        ClassLoader classLoader = getClass().getClassLoader();
        URL zipFilePath = classLoader.getResource(fileName);
        File fileForUpload = new File(zipFilePath.getPath());
        FileInputStream inputStream = new FileInputStream(fileForUpload);
        MultipartFile fileForArticleService = new MockMultipartFile(fileForUpload.getName(), fileName, fileType, inputStream);
        String path = fileForUpload.getParentFile().getAbsolutePath();
        return assertThrows(Exception.class, () -> articlesService.createArticle(fileForArticleService, category, 1L, path));
    }

    private List<ArticleDto> getArticlesListFromService(String fileName, int maxCountOfFilesInZip) throws IOException, IllegalAccessException {
        FieldUtils.writeField(articlesService, "maxCountOfFilesInZip", maxCountOfFilesInZip, true);
        FieldUtils.writeField(articlesService, "minCountOfLinesInArticleFile", 2, true);
        UserEntity testUser = UserEntity.builder().authorName("TestAuthor").build();
        ArticleEntity articleFromRepository = ArticleEntity.builder()
                .id(new Random().nextLong())
                .headline("Headline")
                .text("Article Body")
                .user(testUser)
                .category(ArticleEntityCategories.CULTURE)
                .publicationDateTime(new Date())
                .build();
        when(this.userService.fetchUserById(any())).thenReturn(testUser);
        when(this.articlesRepository.save(any(ArticleEntity.class))).thenReturn(articleFromRepository);
        ClassLoader classLoader = getClass().getClassLoader();
        URL zipFilePath = classLoader.getResource(fileName);
        File fileForUpload = new File(zipFilePath.getPath());
        FileInputStream inputStream = new FileInputStream(fileForUpload);
        MultipartFile fileForArticleService = new MockMultipartFile(fileForUpload.getName(), fileName, "zip", inputStream);
        String path = fileForUpload.getParentFile().getAbsolutePath();
        return articlesService.createArticle(fileForArticleService, "culture", 1L, path);
    }
}
