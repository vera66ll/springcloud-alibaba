package com.itheima.service.fallback;

import com.itheima.domain.Product;
import com.itheima.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

//这是一个容错类，需要实现feign所在的接口，并实现接口中所有的方法
//一旦feign远程调用出现问题，就会进入当前类名中的同名方法，执行容错逻辑
@Service //放到bean容器里面，才不会报错误:No fallback instance of type class com.itheima.service.fallback.ProductServiceFallback found for feign client service-product
public class ProductServiceFallback implements ProductService {
    @Override
    public Product findByPid(Integer pid){
        //容错逻辑
        Product product = new Product();
        product.setPid(-100);
        product.setPname("远程调用商品微服务出现异常，进入容错逻辑");
        return product;
    }
}
