package com.github.zy.netty.rpc.strategy.server;

import com.alibaba.fastjson.JSON;
import com.github.zy.netty.rpc.common.protocol.DefaultMessagePacket;
import com.github.zy.netty.rpc.common.protocol.MessagePacketBuilder;
import com.github.zy.netty.rpc.common.protocol.MessageType;
import com.github.zy.netty.rpc.common.protocol.ResponseCode;
import com.github.zy.netty.rpc.config.RpcServerContextHolder;
import com.github.zy.netty.rpc.domain.RpcMappingBean;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * @version 1.0 created by zy on 2020/4/28 0:34
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ServerRequestMessageResolver implements ServerMessageResolverStrategy {

    private final RpcServerContextHolder contextHolder;

    @Override
    public boolean support(MessageType messageType) {
        return MessageType.REQUEST.equals(messageType);
    }

    @Override
    public void resolver(ChannelHandlerContext ctx, DefaultMessagePacket msg) {
        //处理请求消息，分发到对应的映射中去
        DefaultMessagePacket response = null;
        try {
            String requestUrlMapping = msg.getMappingUrl();
            Optional<RpcMappingBean> optional = contextHolder.getServiceByUrlMapping(requestUrlMapping);
            if (optional.isPresent()) {
                RpcMappingBean rpcMappingBean = optional.get();

                Method targetMethod = rpcMappingBean.getTargetMethod();
                Object targetInstance = rpcMappingBean.getTargetInstance();

                List<RpcMappingBean.RpcMethodParam> methodParams = rpcMappingBean.getMethodParams();
                validateMethodParams(methodParams);

                RpcMappingBean.RpcMethodParam rpcMethodParam = methodParams.get(0);
                Class<?> paramType = rpcMethodParam.getParamType();

                Object returnValue = targetMethod.invoke(targetInstance, JSON.parseObject(msg.getPayload(), paramType));
                if (rpcMappingBean.isNeedReturn()) {
                    //如果需要返回值，那么就将数据返回
                    response = MessagePacketBuilder.buildBasicResponse().responseCode(ResponseCode.SUCCESS).serialNumber(msg.getSerialNumber())
                            .payload(JSON.toJSONString(returnValue)).build();
                } else {
                    response = MessagePacketBuilder.buildBasicResponse().responseCode(ResponseCode.SUCCESS).serialNumber(msg.getSerialNumber()).build();
                }
            } else {
                response = MessagePacketBuilder.buildBasicResponse().responseCode(ResponseCode.NOT_FOUND).serialNumber(msg.getSerialNumber()).build();
            }
        } catch (Exception e) {
            response = MessagePacketBuilder.buildBasicResponse().responseCode(ResponseCode.ERROR).serialNumber(msg.getSerialNumber()).build();
            log.error("处理客户端请求发生异常", e);
        } finally {
            ctx.channel().writeAndFlush(response);
        }
    }
}
