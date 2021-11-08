package com.exam.springnews.service;

import com.exam.springnews.dto.UserDto;
import com.exam.springnews.exceptions.CustomApplicationException;
import com.exam.springnews.exceptions.UserRepositoryException;
import com.exam.springnews.persistence.UsersRepository;
import com.exam.springnews.persistence.entity.user.UserEntity;
import com.exam.springnews.persistence.entity.user.UserEntityRoles;
import com.exam.springnews.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersServiceImpl implements UserService {
    private final UsersRepository usersRepository;

    public UsersServiceImpl(@Autowired UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public List<UserDto> fetchAuthors() {
        Optional<List<UserEntity>> users = usersRepository.findAllByRoleOrderByAuthorName(UserEntityRoles.AUTHOR);
        return users.map(UserUtils::toDtoListConverter).orElse(null);
    }

    @Override
    public UserEntity fetchUserById(Long id) throws CustomApplicationException {
        Optional<UserEntity> user = usersRepository.findById(id);
        if (user.isPresent()) return user.get();
        else throw new UserRepositoryException("User with ID=" + id + " not found");
    }
}
