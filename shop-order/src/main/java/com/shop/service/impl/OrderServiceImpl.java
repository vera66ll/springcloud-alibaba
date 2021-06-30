package com.shop.service.impl;

import com.shop.dao.OrderDao;
import com.shop.domain.Order;
import com.shop.domain.Product;
import com.shop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service  //服务方法放入容器bean中，才不会报错Error creating bean with name
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;
    @Override
    public void createOrder(Order order) {
        orderDao.save(order); //存储到数据库中
    }
    @Override
    public Order setOrder(Integer Uid, String name, Integer pid, Integer pnum, Product product){
        //创建商品订单内容
        Order order = new Order();
        order.setUid(Uid);
        order.setUsername(name);
        order.setPid(pid);
        order.setPname(product.getPname());
        order.setPprice(product.getPprice());
        order.setNumber(pnum);
        return order;
    }
}
