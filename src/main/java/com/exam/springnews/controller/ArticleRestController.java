package com.exam.springnews.controller;

import com.exam.springnews.dto.FileUploadDto;
import com.exam.springnews.service.ArticlesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/articles")
public class ArticleRestController {

    private final ArticlesService articlesService;

    public ArticleRestController(ArticlesService articlesService) {
        this.articlesService = articlesService;
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
    public ResponseEntity<?> uploadArticle(@RequestParam("file") MultipartFile file, @RequestParam("authorId") Long authorId, @RequestParam("category") String category) {
        return ResponseEntity.
                status(HttpStatus.CREATED).
                body(articlesService.createArticle(
                        file,
                        category,
                        authorId,
                        "dsdsd"));
    }
}
