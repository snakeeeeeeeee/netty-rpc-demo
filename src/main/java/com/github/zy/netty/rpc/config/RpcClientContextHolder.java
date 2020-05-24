package com.github.zy.netty.rpc.config;

import com.github.zy.netty.rpc.utils.RpcMappingUtil;
import com.github.zy.netty.rpc.annotation.RpcClient;
import com.github.zy.netty.rpc.domain.RpcMappingBean;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;

import java.util.*;

/**
 * @version 1.0 created by zy on 2020/4/27 11:21
 */
public class RpcClientContextHolder {
    private final Map<String, RpcMappingBean> rpcMappingap = new HashMap<>();
    private final ApplicationContext applicationContext;

    public RpcClientContextHolder(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        initRpcClientBeans();
    }

    public Optional<RpcMappingBean> getClientByUrlMapping(String url){
        return Optional.ofNullable(rpcMappingap.get(url));
    }

    private void initRpcClientBeans(){
        Map<String, Object> rpcMappingBeanMap = applicationContext.getBeansWithAnnotation(RpcClient.class);
        RpcMappingUtil.resolveMapping(rpcMappingBeanMap, rpcMappingap);
    }
}
