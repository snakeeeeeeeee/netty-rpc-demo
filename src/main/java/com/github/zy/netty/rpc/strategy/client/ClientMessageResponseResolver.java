package com.github.zy.netty.rpc.strategy.client;

import com.alibaba.fastjson.JSON;
import com.github.zy.netty.rpc.common.protocol.DefaultMessagePacket;
import com.github.zy.netty.rpc.common.protocol.MessageType;
import com.github.zy.netty.rpc.domain.MessageResponseModule;
import com.github.zy.netty.rpc.sync.SyncFuture;
import com.github.zy.netty.rpc.sync.SyncFutureHolder;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @version 1.0 created by zy on 2020/4/29 0:03
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClientMessageResponseResolver implements ClientMessageResolverStrategy {
    @Override
    public boolean support(MessageType messageType) {
        return MessageType.RESPONSE.equals(messageType);
    }

    @Override
    public void resolver(ChannelHandlerContext ctx, DefaultMessagePacket msg) {
        log.debug("接收到响应包,内容:{}", JSON.toJSONString(msg));
        MessageResponseModule responseModule = new MessageResponseModule();
        responseModule.setCode(msg.getResponseCode());
        responseModule.setInfo(msg.getResponseDesc());
        responseModule.setPayload(msg.getPayload());

        SyncFuture syncFuture = SyncFutureHolder.SYNC_FUTURE_MAP.get(msg.getSerialNumber());
        syncFuture.setResponse(responseModule);
    }
}
