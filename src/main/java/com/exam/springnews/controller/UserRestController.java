package com.exam.springnews.controller;

import com.exam.springnews.dto.UserDto;
import com.exam.springnews.service.UserService;
import com.exam.springnews.utils.UserUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<?> fetchAllUsers() {
        return ResponseEntity.ok(userService.fetchAll());
    }

    @GetMapping("/{role}")
    public ResponseEntity<?> fetchUsersByRole(@PathVariable("role") String role) {
        return ResponseEntity.ok(userService.fetchByUserRole(role));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> fetchUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(new UserDto(userService.fetchUserById(id)));
    }

    @PostMapping("/new")
    public ResponseEntity<?> createUser(@RequestBody UserDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(dto));
    }

}
