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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsersServiceImpl implements UserService {
    private final UsersRepository usersRepository;


    public UsersServiceImpl(@Autowired UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> fetchAuthors() {
        List<UserEntity> users = usersRepository.findAllByRoleOrderByAuthorName(UserEntityRoles.AUTHOR);
        return UserUtils.toDtoListConverter(users);
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity fetchUserById(Long id) throws CustomApplicationException {
        Optional<UserEntity> user = usersRepository.findById(id);
        if (user.isPresent()) return user.get();
        else throw new UserRepositoryException(String.format("User with ID=%d not found", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> fetchAll() {
        return UserUtils.toDtoListConverter(usersRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> fetchByUserRole(String role) {
        if (UserUtils.isValidRole(role)) {
            List<UserEntity> users = usersRepository.findAllByRoleOrderByAuthorName(UserEntityRoles.valueOf(role));
            return UserUtils.toDtoListConverter(users);
        } else {
            throw new UserRepositoryException(String.format("Role= %s do not exist.", role));
        }
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto dto) {
        checkingForSave(dto);
        UserEntity user = UserEntity.builder()
                .login(dto.getLogin())
                .password(dto.getPassword())
                .role(UserEntityRoles.valueOf(dto.getRole()))
                .authorName(dto.getName())
                .build();
        user = usersRepository.save(user);
        return new UserDto(user);
    }


    private void checkingForSave(UserDto dto) {
        Optional<UserEntity> user = usersRepository.findByLogin(dto.getLogin());
        if (user.isPresent())
            throw new UserRepositoryException(String.format("User with login= %s already exist.", dto.getLogin()));
        if (!UserUtils.isValidRole(dto.getRole()))
            throw new UserRepositoryException(String.format("Role= %s do not exist.", dto.getRole()));
    }
}
