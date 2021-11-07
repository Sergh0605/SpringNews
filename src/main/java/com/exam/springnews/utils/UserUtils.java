package com.exam.springnews.utils;

import com.exam.springnews.dto.ArticleDto;
import com.exam.springnews.dto.UserDto;
import com.exam.springnews.exceptions.CustomApplicationException;
import com.exam.springnews.exceptions.CustomFileUploadException;
import com.exam.springnews.persistence.entity.user.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UserUtils {
    public static List<UserDto> toDtoListConverter(List<UserEntity> userEntities) {
        return userEntities.stream().
                filter(Objects::nonNull).
                map(UserDto::new).
                collect(Collectors.toList());
    }
}
