package com.example;

public class UserService {
    private UserRepository userRepository;
    private EmailService emailService;

    public User getUser(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(UserDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        User savedUser = userRepository.save(user);
        emailService.sendWelcomeEmail(savedUser.getEmail());

        return savedUser;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

