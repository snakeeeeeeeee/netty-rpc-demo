package com.github.zy.netty.rpc.proxy;

import com.alibaba.fastjson.JSON;
import com.github.zy.netty.rpc.annotation.RpcMapping;
import com.github.zy.netty.rpc.annotation.RpcRequestClient;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 代理
 *
 * @version 1.0 created by zy on 2020/4/24 0:00
 */
@Slf4j
public class RpcRequestClientProxy<T> implements InvocationHandler {

    private static final String SET_METHOD_PREFIX = "set";

    private Class<T> clazz;
    private RpcRequestClientExecute execute;

    public RpcRequestClientProxy(Class<T> clazz, RpcRequestClientExecute execute) {
        this.clazz = clazz;
        this.execute = execute;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcMapping rpcMapping = method.getAnnotation(RpcMapping.class);
        if (rpcMapping != null) {
            validateParam(args);
            RpcRequestTemplate requestTemplate = RpcRequestTemplate.builder().type(clazz.getAnnotation(RpcRequestClient.class)
                    .targetType()).urlMapping(rpcMapping.url()).payload(JSON.toJSONString(args[0])).build();

            String result = this.execute.ex(requestTemplate);
            return JSON.parseObject(result, method.getReturnType());
        }
        return null;
    }

    private void validateParam(Object[] param) {
        if (param != null && param.length > 1) {
            throw new RuntimeException("方法参数暂时只支持单个,但是找到了" + param.length + "个");
        }
    }

    private String getMethodName(String fieldName){
        String str1 = fieldName.substring(0, 1);
        String str2 = fieldName.substring(1);
        return SET_METHOD_PREFIX + str1.toUpperCase() + str2;
    }
}
