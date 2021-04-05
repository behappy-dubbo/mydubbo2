package org.xiaowu.dubbo.consumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.xiaowu.dubbo.interfaces.PayService;

@RestController
public class ConsumerController {

    @Autowired
    ApplicationContext applicationContext;

    @GetMapping("/{name}")
    public String test(@PathVariable String name){
        PayService payService = applicationContext.getBean(PayService.class);
        return payService.pay(name);
    }
}
