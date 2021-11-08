package com.exam.springnews.service;

import com.exam.springnews.dto.UserDto;
import com.exam.springnews.persistence.entity.user.UserEntity;

import java.util.List;

public interface UserService {
    List<UserDto> fetchAuthors();

    UserEntity fetchUserById(Long id);
}
