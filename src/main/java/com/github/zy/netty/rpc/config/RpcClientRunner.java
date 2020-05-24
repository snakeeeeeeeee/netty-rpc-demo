package com.github.zy.netty.rpc.config;

import com.github.zy.netty.rpc.client.ClientHandler;
import com.github.zy.netty.rpc.client.RpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;


/**
 * @version 1.0 created by zy on 2020/4/24 11:11
 */
@Slf4j
public class RpcClientRunner implements CommandLineRunner {

    private final ClientHandler clientHandler;
    private final RpcClient client;
    private final RpcConfigProperties configProperties;

    public RpcClientRunner(ClientHandler clientHandler, RpcClient client, RpcConfigProperties configProperties) {
        this.clientHandler = clientHandler;
        this.client = client;
        this.configProperties = configProperties;
    }

    public void doStart() {
        client.startClient(clientHandler);
    }


    @Override
    public void run(String... args) throws Exception {
        new Thread(this::doStart).start();
    }

    /**
     * todo 手动触发, 异步监听关闭,这里在建立了连接通道后,返回future,使用者根据future.isSuccess来判断是否已经启动成功(阻塞线程)
     */
    public void start() {
        //防止多次调用该方法,去多次启动
        if (client.getChannelFuture() == null || !client.getChannelFuture().isSuccess()) {
            doStart();
            waitStart();
            //异步监听关闭
            syncClose();
        }
    }


    private void syncClose() {
        //异步监听关闭
        new Thread(() -> {
            try {
                client.getChannelFuture().channel().closeFuture().sync();
            } catch (InterruptedException e) {
                log.error("客户端链接发生异常", e);
            }
        });
    }

    private void waitStart() {
        //最大等待阻塞时间
        long startWaitTime = configProperties.getClient().getStartWaitTime();

        //开始计数时间
        long startTime = System.currentTimeMillis();
        for (; ; ) {
            long currentTime = System.currentTimeMillis() - startTime;

            //如果当前时间已经大于开始计数的时间,那么就跳出本次等待
            if(currentTime > startWaitTime){
                break;
            }

            boolean startSuccess = false;
            if (client.getChannelFuture() != null) {
                startSuccess = client.getChannelFuture().isSuccess();
            }

            if (startSuccess) {
                log.debug("连接netty服务端成功,退出等待.");
                break;
            } else {
                log.debug("连接还未准备就绪,线程阻塞.");
            }
            try {
                Thread.sleep(configProperties.getClient().getStartWaitIntervalTime());
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
