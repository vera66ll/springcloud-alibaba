package com.shop.service.impl;

import com.shop.dao.UserDao;
import com.shop.domain.User;
import com.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

//获取数据库中User的数据
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserService userService;
    private boolean register_flag, login_flag;
    @Override
    public List<User> findByusername(String username){
        return userDao.findByUsername(username);
    }
    @Override
    public boolean exitsusername(String username){
        return userDao.existsByUsername(username);
    }
    @Override
    public void createuser(User user){
        userDao.save(user);
    }
}
