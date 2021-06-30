package com.shop.controller;

import com.alibaba.fastjson.JSON;
import com.shop.domain.Product;
import com.shop.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController  //这里如果使用RestController则只能返回字符串，而不能返回到前端页面
@Slf4j
public class ProductController {
    @Autowired
    private ProductService productService;
    //商品信息查询
    @RequestMapping("/product/{pid}")
    public Product product(@PathVariable("pid") Integer pid){
      log.info("接下来要进行{}号商品信息查询", pid);
      Product product = productService.findByPid(pid);
      log.info("商品信息查询成功，内容为{}", JSON.toJSONString(product));
      return product;
    }
    @GetMapping("/product_dcre")
    public void decrease(@RequestParam("pid") Integer pid, @RequestParam("pnum") Integer pnum){
        productService.decrease_stock(pid,pnum);
        log.info("创建订单成功，{}号商品库存数量减{}件,库存数量剩{}", pid, pnum, productService.findByPid(pid).getStock());
    }
}
