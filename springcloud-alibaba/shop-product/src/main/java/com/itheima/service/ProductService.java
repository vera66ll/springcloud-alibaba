package com.itheima.service;

import com.itheima.domain.Product;

import javax.persistence.criteria.CriteriaBuilder;

public interface ProductService {
    //根据pid查询商品信息
    Product findByPid(Integer pid);
    //库存减1
//    Product decrease_stock(Integer pid);
}
