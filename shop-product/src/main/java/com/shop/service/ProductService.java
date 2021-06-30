package com.shop.service;

import com.shop.domain.Product;

import java.util.List;

public interface ProductService {
    //根据pid查询商品信息
    Product findByPid(Integer pid);
    //库存减1
    void decrease_stock(Integer pid, Integer pnum);
    List<Product> findallproduct();
}
