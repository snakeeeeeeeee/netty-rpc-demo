package com.github.zy.netty.rpc.strategy.server;

import com.github.zy.netty.rpc.common.protocol.DefaultMessagePacket;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

/**
 * @version 1.0 created by zy on 2020/4/28 1:03
 */
@RequiredArgsConstructor
public class ServerMessageHandle {

    private final List<ServerMessageResolverStrategy> strategies;

    public void handle(ChannelHandlerContext ctx, DefaultMessagePacket msg) {
        Optional<ServerMessageResolverStrategy> optional = strategies.stream().filter(strategie -> strategie.support(msg.getMessageType())).findFirst();
        if (optional.isPresent()) {
            optional.get().resolver(ctx, msg);
        }
    }
}
