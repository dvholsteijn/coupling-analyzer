package com.example;

public interface UserRepository {
    User findById(Long id);
    User save(User user);
    void deleteById(Long id);
}

