package com.shop.controller;

import com.shop.dao.UserDao;
import com.shop.domain.User;
import com.shop.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.hibernate.validator.constraints.ParameterScriptAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.util.List;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.uid;

@Controller
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    boolean register_flag,login_flag;
    @RequestMapping("/to_login")
    public Object to_login(){
        return "login";
    }

    @RequestMapping("/to_register")
    public Object to_register(){
        return "register";
    }

    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    public String login(HttpServletRequest request, Model model) {
        //获取参数
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        if (!name.isEmpty() && !password.isEmpty()) {
            if (userService.exitsusername(name)) {  //判断用户是否存在
                List<User> user = userService.findByusername(name);
                User user1 = user.get(0);
                if (user1.getPassword().equals(password)) {//判断密码是否正确
                    Integer uid = user1.getUid();  //获用户的uid
                    model.addAttribute("name", name);
                    model.addAttribute("uid", uid);
                    return "do_login";
                } else {
                    login_flag = true;
                    model.addAttribute("login_flag",login_flag);
                    return "login";  //转到登录页面
                }
            }else{
                register_flag = true;
                model.addAttribute("register_flag", register_flag);
                return "login"; //用户不存在请重新输入或点击register注册
            }
        }
        return "login";  //转到登录页面
    }
    @RequestMapping(value = "/register", method = {RequestMethod.POST})
    public String register(@RequestParam("name") String name, @RequestParam("password") String password, @RequestParam("telephone") String telephone, Model model){
        User user = new User();
        if(!name.isEmpty()&& !password.isEmpty()&& !telephone.isEmpty()){
            user.setUsername(name);
            user.setPassword(password);
            user.setTelephone(telephone);
            userService.createuser(user);
            return "login";
        }
        return "register";
    }
}
