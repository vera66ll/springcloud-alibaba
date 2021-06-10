package com.itheima.controller;

import com.alibaba.fastjson.JSON;
import com.itheima.domain.Order;
import com.itheima.domain.Product;
import com.itheima.service.OrderService;
import com.itheima.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

@RestController //controller中只能注释一个@RestController
@Slf4j
public class OrderController {
    //服务调用的restTemplate
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private OrderService orderService;
    //nacos服务注册中心给的发现服务对象
    @Autowired
    private DiscoveryClient discoveryClient;
    @Resource
    private RocketMQTemplate rocketMQTemplate;
    //通过Feign调用微服务
    @Resource
    /*使用@Autowired报错Could not autowire. There is more than one bean of 'ProductService' type.
      解决方法：这里首先要简短的说明一下@Autowired与@Resource(name="")的区别,
      两个注解都是用于实现Spring中ioc功能，也就是注入对象，但是两者也有一个本质的区别，
      @Autowired对象是通过对象类型注入对象的。所以，如果ProductService接口下实现了多个对象，
      @Autowired就不能准确的指定到底是哪一个实现类，所以会报如上错误.
      所以改为@Resource,从Spring容器中搜索属于ProductService类型的对象，包括实现它的一些对象
    * */
    private ProductService productService; //在service中创建了ProductService接口
    //下单
    @RequestMapping("/order/prob/{pid}")
    public Order order(@PathVariable("pid") Integer pid) {
        log.info("接收到{}商品的下单请求，接下来调用商品微服务查询此商品信息", pid);
        //调用商品微服务
        //.................方法1：通过restTemplate调用商品微服务
        /*
        问题：1.通过restTemplate调用微服务，一旦服务提供者的地址信息变化了，就不得不去修改服务调用者的代码
             2. 一旦服务提供者做了集群，服务调用者一方无法实现负载均衡去调用
             3. 一旦微服务变得越来越多，如何来管理服务清单就成了问题。
             解决：通过注册中心实现服务治理，服务治理是微服务架构中实现各个微服务的自动化注册和发现
             nacos:负责服务发现和配置，相当于eureka+config
        * */
//        Product product = restTemplate.getForObject("http://localhost:8081/product/" + pid, Product.class);

        //.....................方法2：获取微服务实例，并随机实现负载均衡
//        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
//        //随机获取获取微服务的一个服务实例，对应的是端口号
//        int index = new Random().nextInt(instances.size());
//        ServiceInstance instance = instances.get(index);
//        // instance.getHost()获取微服务主机名， instance.getPort()获取微服务端口号
//        Product product = restTemplate.getForObject( "http://"+instance.getHost()+":"+instance.getPort()+ "/product/" + pid, Product.class);
        //.....................方法3：Ribbon实现负载均衡，
        /*方法3：Ribbon实现负载均衡，
        需要更改的内容：
            1.在restTemplate内添加注解@LoadBalance
            2.在配置文件里加入负载均衡策略
            3.修改OrderController中url，url中主机名和端口号换成微服务名称service-product
        * */
//        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
//        Product product = restTemplate.getForObject( "http://service-product/product/" + pid, Product.class);
//
        //方法4：通过Feign实现负载均衡，调用服务提供者的服务
        Product product = productService.findByPid(pid);
        //配置了容错类后，如果调用的服务提供者的服务失败后，就会执行容错类com.itheima.service.fallback.ProductServiceFallback的方法，返回以下值
        if(product.getPid()==-100){
            Order order = new Order();
            order.setOid(-100L);
            order.setPname("下单失败");
            return order;
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

        orderService.createOrder(order);
        log.info("创建订单成功，订单信息为{}", JSON.toJSONString(order));
        //向mq中投递一个下单成功的消息
        //参数1：指定topic,参数2：指定消息体
        rocketMQTemplate.convertAndSend("order-topic", order);
        return order;
    }
}
