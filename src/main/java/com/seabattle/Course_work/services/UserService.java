package com.seabattle.Course_work.services;

import com.seabattle.Course_work.models.Role;
import com.seabattle.Course_work.models.User;
import com.seabattle.Course_work.repositories.RoleRepository;
import com.seabattle.Course_work.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @PersistenceContext
    private EntityManager em;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // Конструктор для инжекции зависимостей
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;

    }

    public User findUserById(Long userId) {
        Optional<User> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(new User());
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public void saveUser(String login, String password) {
        if (userRepository.findByLogin(login).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует");
        }
        User newUser = new User();
        newUser.setLogin(login);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRoles(Collections.singleton(new Role(1L, "ROLE_USER")));
        userRepository.save(newUser);
    }
    public boolean deleteUser(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }
    public List<User> usergtList(Long idMin) {
        return em.createQuery("SELECT u FROM User u WHERE u.id > :paramId", User.class)
                .setParameter("paramId", idMin)
                .getResultList();
    }
    public void updateLoses(String username)
    {
        User user = userRepository.findByLogin(username).get();
        user.setDefeats(user.getDefeats() + 1);
        userRepository.save(user);
    }
    public void updateWins(String username)
    {
        User user = userRepository.findByLogin(username).get();
        user.setWins(user.getWins() + 1);
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(username).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(), user.getRoles());
    }

    public boolean isValidUser(String username, String password) {
        Optional<User> user = userRepository.findByLogin(username);
        if (user.isPresent()) {
            // Сравниваем пароли (с учетом кодировки)
            return passwordEncoder.matches(password, user.get().getPassword());
        }
        return false;
    }

}