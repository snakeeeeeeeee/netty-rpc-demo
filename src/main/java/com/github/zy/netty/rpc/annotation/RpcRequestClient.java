package com.github.zy.netty.rpc.annotation;

import com.github.zy.netty.rpc.constants.RpcRequestClientType;

import java.lang.annotation.*;

/**
 * @version 1.0 created by zy on 2020/4/26 9:45
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcRequestClient {

    RpcRequestClientType targetType() default RpcRequestClientType.SERVER;

}
