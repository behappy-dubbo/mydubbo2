package org.xiaowu.dubbo.interfaces;

import org.xiaowu.mydubbo.framework.annotations.MyDubboRefrence;

@MyDubboRefrence
public interface PayService {
    String pay(String name);
}
