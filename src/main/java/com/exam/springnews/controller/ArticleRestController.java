package com.exam.springnews.controller;

import com.exam.springnews.service.ArticlesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/articles")
public class ArticleRestController {

    private final ArticlesService articlesService;
    private final HttpServletRequest request;

    public ArticleRestController(ArticlesService articlesService, HttpServletRequest request) {
        this.articlesService = articlesService;
        this.request = request;
    }

    @GetMapping("/")
    public ResponseEntity<?> fetchAllArticles() {
        return ResponseEntity.ok(articlesService.fetchAll());
    }

    @GetMapping("/{category}")
    public ResponseEntity<?> fetchArticlesByCategory(@PathVariable("category") String category) {
        return ResponseEntity.ok(articlesService.fetchByCategory(category));
    }

    @GetMapping("/article/{id}")
    public ResponseEntity<?> fetchArticleById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(articlesService.fetchById(id));
    }

    @PostMapping("/new")
    public ResponseEntity<?> uploadArticle(
            @RequestPart("file") MultipartFile file,
            @RequestParam("authorId") Long authorId,
            @RequestParam("category") String category) {
        String realPathToUploads = request.getServletContext().getRealPath("");
        return ResponseEntity.
                status(HttpStatus.CREATED).
                body(articlesService.createArticle(
                        file,
                        category,
                        authorId,
                        realPathToUploads));
    }
}
