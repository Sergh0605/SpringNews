package com.exam.springnews.utils;


import com.exam.springnews.dto.UserDto;
import com.exam.springnews.persistence.entity.user.UserEntity;

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
}
