package com.shop.service.impl;
import com.alibaba.fastjson.JSON;
import com.shop.dao.UserDao;
import com.shop.domain.User;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import com.shop.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//...............监听MQ消息的类..............
@Slf4j
@Service
@RocketMQMessageListener(consumerGroup = "shop-user", topic = "order-topic")
public class SmsService implements RocketMQListener<Order> {
    @Autowired
    private UserDao userDao;
    @Override
    public void onMessage(Order order) {
        log.info("收到一个订单信息{},接下来发送短信",order);
        Integer uid = order.getUid();
        User user = userDao.findById(order.getUid()).get();  //设置以下数据库，shop-user
        log.info("订单短信：向该{}用户发送已成功购买以下商品:\n{}",JSON.toJSONString(user), JSON.toJSONString(order));
    }
}
