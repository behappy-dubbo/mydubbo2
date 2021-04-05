package org.xiaowu.mydubbo.framework.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class Invocation implements Serializable {
    // 接口名称
    String interfaceName;
    // 方法名
    String methodName;
    // 参数类型列表
    Class[] paramTypes;
    // 参数值列表
    Object[] params;
}