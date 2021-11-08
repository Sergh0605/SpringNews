package com.exam.springnews.dto;

import com.exam.springnews.persistence.entity.user.UserEntity;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String name;
    private String login;
    private Long id;

    public UserDto(UserEntity user) {
        this.name = user.getAuthorName();
        this.login = user.getLogin();
        this.id = user.getId();
    }
}
