package com.shop.controller;

import com.shop.domain.Product;
import com.shop.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@Slf4j
public class testcontroller {
    @Autowired
    ProductService productService;
    //使所有商品进入前端页面
    @RequestMapping(value = "/productlist",method = {RequestMethod.GET})
    public Object productlist(@RequestParam("name") String name, @RequestParam("uid") String uid, Model model){
        List<Product> products = productService.findallproduct();
        model.addAttribute("products",products);
        model.addAttribute("name",name);
        model.addAttribute("uid",uid);
        return "test";
    }
}
