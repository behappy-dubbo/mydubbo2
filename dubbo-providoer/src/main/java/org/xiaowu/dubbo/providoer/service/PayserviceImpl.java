package org.xiaowu.dubbo.providoer.service;

import org.xiaowu.dubbo.interfaces.PayService;
import org.xiaowu.mydubbo.framework.annotations.MyDubboService;

@MyDubboService(PayService.class)
public class PayserviceImpl implements PayService {
    @Override
    public String pay(String name) {
        return name+": 支付100元";
    }
}
