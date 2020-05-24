package com.github.zy.netty.rpc.service;

import com.github.zy.netty.rpc.common.protocol.DefaultMessagePacket;
import com.github.zy.netty.rpc.config.RpcConfigProperties;
import com.github.zy.netty.rpc.utils.ChannelHandlerContextUtil;
import com.github.zy.netty.rpc.session.SessionManager;
import com.github.zy.netty.rpc.strategy.server.ServerMessageHandle;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @version 1.0 created by zy on 2020/4/23 16:37
 */
@Slf4j
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<DefaultMessagePacket> {

    private final SessionManager sessionManager;
    private final ServerMessageHandle serverMessageHandle;
    private final RpcConfigProperties configProperties;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultMessagePacket msg) throws Exception {
        serverMessageHandle.handle(ctx, msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if(IdleState.READER_IDLE == state){
                //规定时间未收到客户端数据(如果有心跳交互的话就不会走到这来)那么就关闭客户端的channel
                ctx.close();
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ChannelHandlerContextUtil contextUtil = ChannelHandlerContextUtil.INSTANCE;
        String ip = contextUtil.getIp(ctx);
        int port = contextUtil.getPort(ctx);
        log.debug("和客户端建立连接,目标 ip : {}, port : {}", ip, port);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        //断开连接的时候可以去将session清理掉
        ChannelHandlerContextUtil contextUtil = ChannelHandlerContextUtil.INSTANCE;
        String ip = contextUtil.getIp(ctx);
        int port = contextUtil.getPort(ctx);
        //sessionManager.delete(SessionHelper.getSessionId(configProperties.getClient().getSystemId(), ip));
        ctx.close();
        log.error("和客户端断开连接,目标 ip: {} , port : {}", ip, port);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务端的异常情况," , cause);
    }
}
