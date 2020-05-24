package com.github.zy.netty.rpc.config;

import com.github.zy.netty.rpc.client.ClientHandler;
import com.github.zy.netty.rpc.client.RpcClient;
import com.github.zy.netty.rpc.proxy.RpcBeanDefinitionRegistry;
import com.github.zy.netty.rpc.service.CleanSessionService;
import com.github.zy.netty.rpc.session.SessionStorage;
import com.github.zy.netty.rpc.session.SimpleNativeCacheSessionStorage;
import com.github.zy.netty.rpc.service.ServerHandler;
import com.github.zy.netty.rpc.session.SessionManager;
import com.github.zy.netty.rpc.session.DefaultSessionManager;
import com.github.zy.netty.rpc.strategy.client.ClientMessageHandle;
import com.github.zy.netty.rpc.strategy.client.ClientMessageResolverStrategy;
import com.github.zy.netty.rpc.strategy.server.ServerMessageHandle;
import com.github.zy.netty.rpc.strategy.server.ServerMessageResolverStrategy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @version 1.0 created by zy on 2020/4/23 16:29
 */
@Data
@Slf4j
@Configuration
@EnableConfigurationProperties(value = RpcConfigProperties.class)
public class RpcAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "com.github.zy.netty.rpc.server", name = "enable", havingValue = "true")
    public RpcServerRunner serverBootstrap(RpcConfigProperties rpcConfigProperties, ServerHandler serverHandler) {
        return new RpcServerRunner(rpcConfigProperties, serverHandler);
    }

    @Bean
    @ConditionalOnProperty(prefix = "com.github.zy.netty.rpc.client", name = "enable", havingValue = "true")
    public RpcClientRunner clientBootstrap(ClientHandler clientHandler, RpcClient client, RpcConfigProperties configProperties) {
        return new RpcClientRunner(clientHandler, client, configProperties);
    }

    @Bean
    @ConditionalOnMissingBean(SessionManager.class)
    public SessionManager sessionManager(SessionStorage sessionStorage) {
        return new DefaultSessionManager(sessionStorage);
    }

    @Bean
    @ConditionalOnMissingBean(SessionStorage.class)
    public SessionStorage sessionStorage() {
        return new SimpleNativeCacheSessionStorage();
    }

    @Bean
    public RpcClientContextHolder rpcClientContextHolder(ApplicationContext applicationContext) {
        return new RpcClientContextHolder(applicationContext);
    }

    @Bean
    public RpcServerContextHolder rpcServerContextHolder(ApplicationContext applicationContext, RpcConfigProperties rpcConfigProperties) {
        return new RpcServerContextHolder(applicationContext, rpcConfigProperties);
    }

    @Bean
    @ConditionalOnBean(RpcClientContextHolder.class)
    public ClientHandler clientHandler(ClientMessageHandle clientMessageHandle, RpcConfigProperties rpcConfigProperties, RpcClient rpcClient) {
        return new ClientHandler(clientMessageHandle, rpcConfigProperties, rpcClient);
    }

    @Bean
    @ConditionalOnBean(RpcServerContextHolder.class)
    public ServerHandler serverHandler(SessionManager sessionManager, ServerMessageHandle serverMessageHandle, RpcConfigProperties configProperties) {
        return new ServerHandler(sessionManager, serverMessageHandle, configProperties);
    }

    @Bean
    public RpcClient rpcClient(RpcConfigProperties rpcConfigProperties) {
        return new RpcClient(rpcConfigProperties);
    }

    @Bean
    public ServerMessageHandle serverMessageHandle(List<ServerMessageResolverStrategy> strategies) {
        return new ServerMessageHandle(strategies);
    }

    @Bean
    public ClientMessageHandle clientMessageHandle(List<ClientMessageResolverStrategy> strategies) {
        return new ClientMessageHandle(strategies);
    }

    @Bean
    @ConditionalOnBean(RpcServerRunner.class)
    public CleanSessionService syncTaskExecutor(SessionStorage sessionStorage, ApplicationContext applicationContext) {
        return new CleanSessionService(sessionStorage, applicationContext);
    }

    @Bean
    public RpcBeanDefinitionRegistry rpcBeanDefinitionRegistry(){
        return new RpcBeanDefinitionRegistry();
    }
}
