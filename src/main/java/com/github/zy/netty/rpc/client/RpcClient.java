package com.github.zy.netty.rpc.client;

import com.github.zy.netty.rpc.common.protocol.MessagePacketDecoder;
import com.github.zy.netty.rpc.common.protocol.MessagePacketEncoder;
import com.github.zy.netty.rpc.common.protocol.ValidatePacketHandler;
import com.github.zy.netty.rpc.common.serialize.FastJsonSerializer;
import com.github.zy.netty.rpc.config.RpcConfigProperties;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @version 1.0 created by zy on 2020/4/30 14:37
 */
@Slf4j
@Data
@RequiredArgsConstructor
public class RpcClient {

    private final RpcConfigProperties rpcConfigProperties;
    private ClientHandler clientHandler;
    private ChannelFuture channelFuture;


    public void startClient(ClientHandler handler) {
        this.clientHandler = handler;
        int port = rpcConfigProperties.getClient().getPort();
        String ip = rpcConfigProperties.getClient().getIp();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);
            bootstrap.option(ChannelOption.TCP_NODELAY, Boolean.TRUE);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(2048, 0, 4, 0, 4));
                    ch.pipeline().addLast(new LengthFieldPrepender(4));
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new MessagePacketDecoder());
                    ch.pipeline().addLast(new MessagePacketEncoder(FastJsonSerializer.INSTANCE));
                    ch.pipeline().addLast(new IdleStateHandler(rpcConfigProperties.getClient().getDisconnect()
                            , rpcConfigProperties.getClient().getDisconnect(), rpcConfigProperties.getClient().getHeartInterval()));
                    ch.pipeline().addLast(new ValidatePacketHandler());
                    ch.pipeline().addLast(clientHandler);
                }
            });
            channelFuture = bootstrap.connect(ip, port).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        future.channel().eventLoop().schedule(() -> {
                            log.debug("正在重新连接服务端 目标 ip：{}， port：{}", ip, port);
                            startClient(clientHandler);
                        }, rpcConfigProperties.getClient().getDisconnectRetryInterval(), TimeUnit.SECONDS);
                    }
                }
            }).sync();
        } catch (Exception e) {
            log.error("连接服务端 失败： ip：{}， port：{}", ip, port, e);
        }
    }


}
