package com.github.zy.netty.rpc.strategy.client;

import com.github.zy.netty.rpc.common.protocol.DefaultMessagePacket;
import com.github.zy.netty.rpc.common.protocol.MessageType;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @version 1.0 created by zy on 2020/4/28 0:42
 */
@Slf4j
@Component
public class PongMessageResolver implements ClientMessageResolverStrategy {
    @Override
    public boolean support(MessageType messageType) {
        return MessageType.PONG.equals(messageType);
    }

    @Override
    public void resolver(ChannelHandlerContext ctx, DefaultMessagePacket msg) {
        //处理回应ping的策略
        log.debug("收到服务端心跳回复...");
    }
}
