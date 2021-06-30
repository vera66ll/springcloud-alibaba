package com.shop.controller;

import com.alibaba.fastjson.JSON;
import com.shop.domain.Order;
import com.shop.domain.Product;
import com.shop.service.OrderService;
import com.shop.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

//.........................模拟高并发....................
/*
1.在order方法中添加了线程延迟2秒
2.创建了另一个message方法
3.通过apache模拟高并发，使message方法无法访问。造成了雪崩效应
* */
//@RestController
@Slf4j
public class OrderController2 {
    @Autowired
    private OrderService orderService;
    //通过Feign调用提供者的微服务
    @Autowired
    private ProductService productService; //在service中创建了ProductService接口
    //..............下单方法..............
    @RequestMapping("/order/prob/{pid}")
    public Order order(@PathVariable("pid") Integer pid) {
        log.info("接收到{}商品的下单请求，接下来调用商品微服务查询此商品信息", pid);
        //调用商品微服务
        //通过Feign实现负载均衡，调用服务提供者的服务
        Product product = productService.findByPid(pid);
        //模拟调用商品微服务需要2秒中的时间
        try {
            Thread.sleep(20001);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("查询到{}号商品的信息，内容是：{}", pid, JSON.toJSONString(product));
        //下单
        Order order = new Order();
        order.setUid(1);
        order.setUsername("测试用户");
        order.setPid(pid);
        order.setPname(product.getPname());
        order.setPprice(product.getPprice());
        order.setNumber(1);
        //为了不产生大量的额外垃圾数据，注释掉在数据库中创建订单
//        orderService.createOrder(order);
        log.info("创建订单成功，订单信息为{}", JSON.toJSONString(order));
        return order;
    }
    //..........测试高并发.............
    @RequestMapping("/order/message")
    public String message(){
        return "测试高并发";
    }
}
