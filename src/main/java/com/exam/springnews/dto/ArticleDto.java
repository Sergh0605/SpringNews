package com.exam.springnews.dto;

import com.exam.springnews.persistence.entity.article.ArticleEntity;
import lombok.*;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    private Date publicationDateTime;

    public ArticleDto(ArticleEntity article) {
        this.id = article.getId();
        this.headline = article.getHeadline();
        this.fullText = article.getText();
        this.author = article.getUser().getAuthorName();
        this.shortText = generateShortText(fullText);
        this.publicationDateTime = article.getPublicationDateTime();
    }

    private String generateShortText(String fullText) {
        String suffix = "...";
        if (fullText.length() > 201) {
            return fullText.substring(0, 200) + suffix;
        }
        return fullText + suffix;
    }

    public String getPublicationTimeWithFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy   HH:mm");
        return dateFormat.format(getPublicationDateTime());
    }
}
