package com.exam.springnews;

import com.exam.springnews.dto.ArticleDto;
import com.exam.springnews.persistence.ArticlesRepository;
import com.exam.springnews.persistence.entity.article.ArticleEntity;
import com.exam.springnews.persistence.entity.article.ArticleEntityCategories;
import com.exam.springnews.persistence.entity.user.UserEntity;
import com.exam.springnews.service.ArticleServiceImpl;
import com.exam.springnews.service.ArticlesService;
import com.exam.springnews.service.UserService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.Before;
import org.junit.Test;
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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArticleServiceTests {

    @Mock
    private ArticlesRepository articlesRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ArticleServiceImpl articlesService;

/*    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }*/

    @Test
    public void testCreateArticle() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL zipFilePath = classLoader.getResource("emptyZip.zip");
        File fileForUpload = new File(zipFilePath.getPath());
        String path = fileForUpload.getParentFile().getAbsolutePath();
        FileInputStream inputStream = new FileInputStream(fileForUpload);
        MultipartFile multipartFile = new MockMultipartFile(fileForUpload.getName(), inputStream);
        when(this.articlesRepository.save(any(ArticleEntity.class))).thenReturn(new ArticleEntity());
        UserEntity testUser = UserEntity.builder().authorName("TestAuthor").build();
        when(this.userService.fetchUserById(any())).thenReturn(testUser);
        List<ArticleDto> articleDtoList = articlesService.createArticle(multipartFile, ArticleEntityCategories.CULTURE.toString(), 1L, zipFilePath.getPath());
    }
}
