package com.shop.controller;

import com.alibaba.fastjson.JSON;
import com.shop.domain.Order;
import com.shop.domain.Product;
import com.shop.service.OrderService;
import com.shop.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller //controller中只能注释一个@RestController
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
    @RequestMapping(value = "/order/prob",method = {RequestMethod.POST,RequestMethod.GET})
        public Object order(HttpServletRequest request, Model model) {
        //获取请求过来的信息（post）
        String name = request.getParameter("name");
        String str_Uid = request.getParameter("uid");
        int Uid = Integer.parseInt(str_Uid);
        String str_pnum = request.getParameter("pnum");
        int pnum = Integer.parseInt(str_pnum);
        String str_pid = request.getParameter("pid");
        int pid = Integer.parseInt(str_pid);

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
        //创建订单对象
        Order order = orderService.setOrder(Uid, name, pid, pnum, product);
        //判断库存是否存在
        if(product.getStock()<order.getNumber()){
            log.info("{}商品库存不足", order.getPid());
            product.setStock(0);
            model.addAttribute("order",order);
            return "order_fail";
        }
        //下单，存储订单内容至数据库
        orderService.createOrder(order);
        log.info("创建订单成功，订单信息为{}", JSON.toJSONString(order));

        //调用减库存服务
        productService.decrease_stock(pid,pnum);
        model.addAttribute("order",order);
        //..................向mq中投递一个下单成功的消息.................
        //参数1：指定topic,参数2：指定消息体
        //这里需要注意client和server版本一定要一致，本开发版本为4.4.0,不然就会报no route info of this topic
        /*
        开启D:\rocketmq-all-4.4.0-bin-release\bin\mqnamesrv.cmd
        开启broke命令：start mqbroker.cmd -n 127.0.0.1:9876 autoCreateTopicEnable=true
        */
        rocketMQTemplate.convertAndSend("order-topic", order); //暂时屏蔽一下
        return "order_success";
    }
}
