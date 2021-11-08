package com.exam.springnews.persistence;

import com.exam.springnews.persistence.entity.user.UserEntity;
import com.exam.springnews.persistence.entity.user.UserEntityRoles;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends CrudRepository<UserEntity, Long> {
    List<UserEntity> findAllByRoleOrderByAuthorName(UserEntityRoles role);

    Optional<UserEntity> findById(Long id);

    List<UserEntity> findAll();

    Optional<UserEntity> findByLogin(String login);
}
