package com.github.zy.netty.rpc.config;

import com.github.zy.netty.rpc.common.protocol.MessagePacketDecoder;
import com.github.zy.netty.rpc.common.protocol.MessagePacketEncoder;
import com.github.zy.netty.rpc.common.protocol.ValidatePacketHandler;
import com.github.zy.netty.rpc.common.serialize.FastJsonSerializer;
import com.github.zy.netty.rpc.service.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;


/**
 * @version 1.0 created by zy on 2020/4/23 16:29
 */
@Slf4j
public class RpcServerRunner implements CommandLineRunner {

    private final RpcConfigProperties rpcConfigProperties;
    private final ServerHandler serverHandler;

    public RpcServerRunner(RpcConfigProperties rpcConfigProperties, ServerHandler serverHandler){
        this.rpcConfigProperties = rpcConfigProperties;
        this.serverHandler = serverHandler;
    }


    public void startServer() {
        if (rpcConfigProperties.getServer().isEnable()) {
            log.info("开始启动NettyServer...");
            int port = rpcConfigProperties.getServer().getPort();
            ServerBootstrap bootstrap = new ServerBootstrap();
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(2048, 0, 4, 0, 4));
                                ch.pipeline().addLast(new LengthFieldPrepender(4));
                                ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                                ch.pipeline().addLast(new MessagePacketDecoder());
                                ch.pipeline().addLast(new MessagePacketEncoder(FastJsonSerializer.INSTANCE));
                                ch.pipeline().addLast(new IdleStateHandler(0,0,rpcConfigProperties.getServer().getDisconnectInterval()));
                                ch.pipeline().addLast(new ValidatePacketHandler());
                                ch.pipeline().addLast(serverHandler);
                            }
                        });
                ChannelFuture future = bootstrap.bind(port).sync();
                log.info("启动NettyServer[{}]成功...", port);
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                log.error("启动NettyServer[" + port + "]失败...", e);
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }
    }


    @Override
    public void run(String... args) throws Exception {
        new Thread(this::startServer).start();
    }
}
