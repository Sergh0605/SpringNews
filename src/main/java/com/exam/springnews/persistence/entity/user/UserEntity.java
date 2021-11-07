package com.exam.springnews.persistence.entity.user;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserEntityRoles role;

    @Column(name = "name")
    private String authorName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntity that)) return false;
        return id.equals(that.id) &&
                login.equals(that.login) &&
                password.equals(that.password) &&
                role == that.role &&
                Objects.equals(authorName, that.authorName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, password, role, authorName);
    }
}
