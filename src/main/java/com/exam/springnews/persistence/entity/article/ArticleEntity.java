package com.exam.springnews.persistence.entity.article;

import com.exam.springnews.persistence.entity.user.UserEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "article")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "headline", nullable = false)
    private String headline;

    @Column(name = "text", nullable = false)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private UserEntity user;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private ArticleEntityCategories category;

    @Column(name = "dateTime_publication", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date publicationDateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleEntity that)) return false;
        return Objects.equals(id, that.id) &&
                Objects.equals(headline, that.headline) &&
                Objects.equals(text, that.text) &&
                Objects.equals(user, that.user) &&
                category == that.category &&
                Objects.equals(publicationDateTime, that.publicationDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, headline, text, user, category, publicationDateTime);
    }
}
