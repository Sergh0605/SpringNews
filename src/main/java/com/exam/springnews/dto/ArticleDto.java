package com.exam.springnews.dto;

import com.exam.springnews.persistence.entity.article.ArticleEntity;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDto {
    private Long id;
    private String headline;
    private String shortText;
    private String fullText;
    private String author;
    private LocalDateTime publicationDateTime;

    public ArticleDto(ArticleEntity article) {
        this.id = article.getId();
        this.headline = article.getHeadline();
        this.fullText = article.getText();
        this.author = article.getUser().getAuthorName();
        this.shortText = generateShortText(fullText);
        this.publicationDateTime = new Timestamp(article.getPublicationDateTime().getTime()).toLocalDateTime() ;
    }

    private String generateShortText(String fullText) {
        String suffix = "...";
        if (fullText.length() > 201) {
            return fullText.substring(0, 200) + suffix;
        }
        return fullText + suffix;
    }
}
