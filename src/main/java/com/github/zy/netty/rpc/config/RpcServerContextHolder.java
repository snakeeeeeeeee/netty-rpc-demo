package com.github.zy.netty.rpc.config;

import com.github.zy.netty.rpc.utils.RpcMappingUtil;
import com.github.zy.netty.rpc.annotation.RpcService;
import com.github.zy.netty.rpc.domain.RpcMappingBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @version 1.0 created by zy on 2020/4/27 11:21
 */
@Slf4j
public class RpcServerContextHolder {

    /**
     * key urlMapping , value: RpcMappingBean
     */
    private final Map<String, RpcMappingBean> rpcMppingMap = new HashMap<>();

    private final ApplicationContext applicationContext;
    private final RpcConfigProperties configProperties;

    public RpcServerContextHolder(ApplicationContext applicationContext, RpcConfigProperties configProperties) {
        this.applicationContext = applicationContext;
        this.configProperties = configProperties;
    }

    @PostConstruct
    public void init() {
        initRpcServiceBeans();
    }

    public Optional<RpcMappingBean> getServiceByUrlMapping(String urlMapping) {
        return Optional.ofNullable(rpcMppingMap.get(urlMapping));
    }

    private void initRpcServiceBeans() {
        Map<String, Object> urlMappingMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        RpcMappingUtil.resolveMapping(urlMappingMap, rpcMppingMap);
    }
}
