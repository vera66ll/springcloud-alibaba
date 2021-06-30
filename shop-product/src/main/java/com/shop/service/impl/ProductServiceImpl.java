package com.shop.service.impl;

import com.shop.dao.ProductDao;
import com.shop.domain.Product;
import com.shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductDao productDao;
    @Override
    public Product findByPid(Integer pid){
        return productDao.findById(pid).get();
    }
    @Override
    public void decrease_stock(Integer pid, Integer pnum){
        Product product = productDao.findById(pid).get();
        Integer stock = product.getStock();
        product.setStock(stock-pnum);
        productDao.save(product);
    }
//    //找到所有商品
    @Override
    public List<Product> findallproduct(){
        List<Product> all_product = productDao.findAll();
        return all_product;
    }
}
