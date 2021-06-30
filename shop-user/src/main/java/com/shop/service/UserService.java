package com.shop.service;

import com.shop.dao.UserDao;
import com.shop.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import java.util.List;

public interface UserService {
    List<User> findByusername(String username);
    boolean exitsusername(String username);
    void createuser(User user);
}
