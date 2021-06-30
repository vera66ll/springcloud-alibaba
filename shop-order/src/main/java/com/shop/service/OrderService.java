package com.shop.service;

import com.shop.domain.Order;
import com.shop.domain.Product;

// .............实现order这个微服务的一个下单功能..............
public interface OrderService {
     void createOrder(Order order);
     Order setOrder(Integer Uid, String name, Integer pid, Integer pnum, Product product);
}
