package com.exam.springnews.service;

import com.exam.springnews.dto.UserDto;
import com.exam.springnews.persistence.entity.user.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<UserDto> fetchAuthors();
    UserEntity fetchUserById(Long id);
}
