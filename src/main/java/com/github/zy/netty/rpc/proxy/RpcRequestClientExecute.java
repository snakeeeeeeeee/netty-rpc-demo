package com.github.zy.netty.rpc.proxy;

import com.github.zy.netty.rpc.client.ClientChannelHolder;
import com.github.zy.netty.rpc.sync.SyncFuture;
import com.github.zy.netty.rpc.sync.SyncFutureHolder;
import com.github.zy.netty.rpc.common.protocol.DefaultMessagePacket;
import com.github.zy.netty.rpc.common.protocol.MessagePacketBuilder;
import com.github.zy.netty.rpc.common.protocol.ResponseCode;
import com.github.zy.netty.rpc.config.RpcConfigProperties;
import com.github.zy.netty.rpc.domain.MessageResponseModule;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @version 1.0 created by zy on 2020/4/26 10:15
 */
@Slf4j
@Component
public class RpcRequestClientExecute {

    @Autowired
    private RpcConfigProperties configProperties;


    public String ex(RpcRequestTemplate template) {
        DefaultMessagePacket messagePacket = MessagePacketBuilder.buildBasicRequest()
                .payload(template.getPayload()).systemId(template.getSystemId()).urlMapping(template.getUrlMapping()).build();
        try {
            //客户端请求服务端
            Channel channel = ClientChannelHolder.CHANNEL_REFERENCE.get();
            if(channel == null){
                throw new RuntimeException("未获取到 channel, 可能客户端与服务端已经断开连接......");
            }
            channel.writeAndFlush(messagePacket);
            //完成后，在这里进行一个同步等待，等到客户端接收到数据后，解除后获取数据
            SyncFuture<MessageResponseModule> syncFuture = new SyncFuture();
            SyncFutureHolder.SYNC_FUTURE_MAP.put(messagePacket.getSerialNumber(), syncFuture);

            MessageResponseModule responseModule = syncFuture.get(configProperties.getSyncTimeout(), TimeUnit.SECONDS);
            if (ResponseCode.SUCCESS.getCode() == responseModule.getCode()) {
                return responseModule.getPayload();
            }
            throw new RuntimeException(String.format("请求发生错误, code: %s , info : %s", responseModule.getCode(), responseModule.getInfo()));
        } finally {
            SyncFutureHolder.SYNC_FUTURE_MAP.remove(messagePacket.getSerialNumber());
        }

    }
}
