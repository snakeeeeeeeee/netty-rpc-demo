package com.github.zy.netty.rpc.config;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @version 1.0 created by zy on 2020/4/23 16:30
 */
@Data
@ConfigurationProperties(prefix = "com.github.zy.netty.rpc", ignoreInvalidFields = true)
public class RpcConfigProperties {

    private int syncTimeout = 30;


    private Server server = DEFAULT_SERVER;
    private Client client = DEFAULT_CLIENT;

    private static final Server DEFAULT_SERVER = Server.builder().port(7777).bossThreadGroup(1).disconnectInterval(30).build();
    private static final Client DEFAULT_CLIENT = Client.builder().port(7777).ip("127.0.0.7").systemId("default").startWaitTime(15000)
            .startWaitIntervalTime(3000).disconnectRetry(true)
            .disconnectRetryInterval(20).heartInterval(10).disconnect(30).build();


    @Data
    @Builder
    public static class Server {

        /**
         * 是否启动
         */
        private boolean enable;

        /**
         * 启动监听端口
         */
        private int port;
        /**
         * 链接处理线程数量
         */
        private int bossThreadGroup;

        /**
         * 工作线程
         */
        private int workThreadGroup;

        /**
         * 模块id
         */
        private String moduleId;

        /**
         * 多久未收到客户端心跳就断开连接
         */
        private int disconnectInterval;
    }

    @Data
    @Builder
    public static class Client {

        /**
         * 是否启动
         */
        private boolean enable;

        /**
         * 连接端口
         */
        private int port;

        /**
         * 连接IP
         */
        private String ip;

        /**
         * 工作线程
         */
        private int workThreadGroup;

        /**
         * 模块Id
         */
        private String systemId;


        /**
         * 是否开启重连
         */
        private boolean disconnectRetry;

        /**
         * 与服务端断开连接后重连间隔
         */
        private int disconnectRetryInterval;

        /**
         * 向客户端发送心跳的间隔
         */
        private int heartInterval;

        /**
         * 多久未收到服务端响应就断开连接的时间
         */
        private int disconnect;

        /**
         * 启动最大等待时间
         */
        private long startWaitTime;

        /**
         * 启动等待间隔检查时间
         */
        private long startWaitIntervalTime;

    }
}
