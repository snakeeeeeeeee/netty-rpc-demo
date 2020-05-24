package com.github.zy.netty.rpc.client;

import com.github.zy.netty.rpc.utils.IPUtil;
import com.github.zy.netty.rpc.common.protocol.DefaultMessagePacket;
import com.github.zy.netty.rpc.common.protocol.MessagePacketBuilder;
import com.github.zy.netty.rpc.config.RpcConfigProperties;
import com.github.zy.netty.rpc.strategy.client.ClientMessageHandle;
import com.github.zy.netty.rpc.utils.ChannelHandlerContextUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @version 1.0 created by zy on 2020/4/24 10:53
 */
@RequiredArgsConstructor
@Slf4j
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<DefaultMessagePacket> {


    private final ClientMessageHandle clientMessageHandle;
    private final RpcConfigProperties configProperties;
    private final RpcClient client;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultMessagePacket packet) throws Exception {
        clientMessageHandle.handle(ctx, packet);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ClientChannelHolder.CHANNEL_REFERENCE.set(ctx.channel());
        //建立连接的时候就将自己的moduleId携带到服务端去
        DefaultMessagePacket packet = MessagePacketBuilder.buildBasicReportModuleId().systemId(configProperties.getClient()
                .getSystemId()).targetIp(IPUtil.getAddress()).build();
        ctx.channel().writeAndFlush(packet);
        ChannelHandlerContextUtil contextUtil = ChannelHandlerContextUtil.INSTANCE;
        String ip = contextUtil.getIp(ctx);
        int port = contextUtil.getPort(ctx);
        log.info("和服务端建立连接.... ip : {}, port : {}", ip, port);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();

            switch (state) {
                case READER_IDLE:
                    //规定时间没有读取操作的时候，这里可以当做没有收到服务端的回复，可能服务端挂了了啥的，这里去主动关闭连接，然后触发关闭的回调函数
                    ctx.close();
                    break;
                case WRITER_IDLE:
                    //规定时间没有写入操作的时候
                    break;
                case ALL_IDLE:
                    //在规定时间没有进行读写操作的话，就去向服务端发送一个心跳
                    sendHeartbeatPacket(ctx);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        //当连接关闭的时候会触发该方法，可以在这里进行对服务端的重连
        ctx.channel().eventLoop().schedule(() -> {
            client.startClient(this);
        }, configProperties.getClient().getDisconnectRetryInterval(), TimeUnit.SECONDS);
        log.error("与服务端断开连接。。。。");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("出现未知异常....", cause);
    }


    private void sendHeartbeatPacket(ChannelHandlerContext ctx) {
        DefaultMessagePacket packet = MessagePacketBuilder.buildBasicPing().build();
        ctx.writeAndFlush(packet);
    }

}
