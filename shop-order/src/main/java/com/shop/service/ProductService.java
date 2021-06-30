package com.shop.service;

import com.shop.domain.Product;
import com.shop.service.fallback.ProductServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
// ............指定调用nacos下的那个微服务...................

//value用于指定调用nacos下的哪个微服务
//fallback用于指定当前feign接口的容错类
@FeignClient(value = "service-product", fallback = ProductServiceFallback.class)
public interface ProductService {
    //@FeignClient的value+@RequestMapping的value值，就是完整的请求路径
    @RequestMapping("/product/{pid}") //指定请求的url部分
    Product findByPid(@PathVariable Integer pid);
    @GetMapping("/product_dcre")
    void decrease_stock(@RequestParam("pid") Integer pid, @RequestParam("pnum") Integer pnum);
}
