package com.exam.springnews.utils;


import com.exam.springnews.dto.UserDto;
import com.exam.springnews.persistence.entity.article.ArticleEntityCategories;
import com.exam.springnews.persistence.entity.user.UserEntity;
import com.exam.springnews.persistence.entity.user.UserEntityRoles;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class UserUtils {
    public static List<UserDto> toDtoListConverter(List<UserEntity> userEntities) {
        return userEntities.stream().
                filter(Objects::nonNull).
                map(UserDto::new).
                collect(Collectors.toList());
    }

    public static boolean isValidRole(String role) {
        for (UserEntityRoles c : UserEntityRoles.values()) {
            if (c.name().equalsIgnoreCase(role)) return true;
        }
        return false;
    }
}
