package com.exam.springnews.controller;

import com.exam.springnews.dto.ArticleDto;
import com.exam.springnews.dto.UserDto;
import com.exam.springnews.exceptions.ArticleServiceException;
import com.exam.springnews.exceptions.CustomApplicationException;
import com.exam.springnews.service.ArticleServiceImpl;
import com.exam.springnews.service.ArticlesService;
import com.exam.springnews.service.UserService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Controller
public class ArticlesController {
    private final static Logger log = getLogger(ArticleServiceImpl.class);
    private final ArticlesService articlesService;
    private final UserService userService;
    @Value("${zipProperties.maxCountOfFilesInZip:1}")
    private Integer maxCountOfFilesInZip;
    @Value("${zipProperties.minCountOfLinesInArticleFile:2}")
    private Integer minCountOfLinesInArticleFile;

    @Autowired
    private HttpServletRequest request;

    public ArticlesController(ArticlesService articlesService, UserService userService) {
        this.articlesService = articlesService;
        this.userService = userService;
    }

    @GetMapping(value = {"/", "/index"})
    public String index(Model model) {
        return "redirect:/articles/FRESH";
    }

    @GetMapping(value = "/article/{id}")
    public String getArticle(@PathVariable Integer id, Model model) {
        try {
            ArticleDto article = articlesService.fetchById(id);
            model.addAttribute("article", article);
            return "article";
        } catch (CustomApplicationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            log.debug(e.getMessage());
            return "error";
        }

    }

    @GetMapping(value = "/articles/{category}")
    public String getByCategory(@PathVariable String category, Model model) {
        try {
            List<ArticleDto> articles = articlesService.fetchByCategory(category);
            model.addAttribute("articles", articles);
            model.addAttribute("category", category);
            return "index";
        } catch (CustomApplicationException e) {
            log.debug(e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }

    @GetMapping(value = "/new_article")
    public String goToUploadPage(Model model) {
        List<UserDto> users = userService.fetchAuthors();
        List<String> categories = articlesService.fetchAllCategories();
        model.addAttribute("users", users);
        model.addAttribute("categories", categories);
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadArticle(@RequestParam("file") MultipartFile file,
                                @RequestParam("category") String category,
                                @RequestParam("userId") Long userId,
                                RedirectAttributes attributes) {
        if (file.isEmpty()) {
            attributes.addFlashAttribute("warningMessage", "Please select a file to upload");
            return "redirect:/new_article";
        }
        try {
            String realPathToUploads = request.getServletContext().getRealPath("");
            List<ArticleDto> newArticles = articlesService.createArticle(file, category, userId, maxCountOfFilesInZip, minCountOfLinesInArticleFile, realPathToUploads);
            for (ArticleDto articleDto : newArticles) {
                log.debug(String.format("New article with Id=%s uploaded", articleDto.getId()));
            }
            return "redirect:/article/" + newArticles.get(0).getId();
        } catch (CustomApplicationException e) {
            log.debug(e.getMessage());
            attributes.addFlashAttribute("warningMessage", e.getMessage());
            return "redirect:/new_article";
        }
    }
}