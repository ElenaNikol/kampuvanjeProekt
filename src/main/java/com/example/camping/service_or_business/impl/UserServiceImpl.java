package com.example.camping.service_or_business.impl;

import com.example.camping.model.User;
import com.example.camping.model.exception.UserAlreadyExistsException;
import com.example.camping.model.exception.UserNotFoundException;
import com.example.camping.persistence_or_repository.UserRepository;
import com.example.camping.service_or_business.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findById(String userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public User registerUser(User user) {
        if (this.userRepository.existsById(user.getUsername())) {
            throw new UserAlreadyExistsException(user.getUsername());
        }
        return this.userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(id));
    }
}
