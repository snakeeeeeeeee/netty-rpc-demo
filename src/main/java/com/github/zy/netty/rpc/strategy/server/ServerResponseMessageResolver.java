package com.github.zy.netty.rpc.strategy.server;

import com.github.zy.netty.rpc.common.protocol.DefaultMessagePacket;
import com.github.zy.netty.rpc.common.protocol.MessageType;
import com.github.zy.netty.rpc.domain.MessageResponseModule;
import com.github.zy.netty.rpc.sync.SyncFuture;
import com.github.zy.netty.rpc.sync.SyncFutureHolder;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @version 1.0 created by zy on 2020/4/28 0:36
 */
@Slf4j
@Component
public class ServerResponseMessageResolver implements ServerMessageResolverStrategy {
    @Override
    public boolean support(MessageType messageType) {
        return MessageType.RESPONSE.equals(messageType);
    }

    @Override
    public void resolver(ChannelHandlerContext ctx, DefaultMessagePacket msg) {
        //包装返回对象
        MessageResponseModule responseModule = new MessageResponseModule();
        responseModule.setCode(msg.getResponseCode());
        responseModule.setInfo(msg.getResponseDesc());
        responseModule.setPayload(msg.getPayload());

        SyncFuture syncFuture = SyncFutureHolder.SYNC_FUTURE_MAP.get(msg.getSerialNumber());
        syncFuture.setResponse(responseModule);
    }
}
