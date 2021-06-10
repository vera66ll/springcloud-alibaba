package com.itheima.service;

import com.itheima.domain.Order;
// .............实现order这个微服务的一个下单功能..............
public interface OrderService {
    public void createOrder(Order order);
}
