package com.shop.dao;

import com.shop.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserDao extends JpaRepository<User, Integer> {
    List<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
