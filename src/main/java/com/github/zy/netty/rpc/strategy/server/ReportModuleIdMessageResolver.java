package com.github.zy.netty.rpc.strategy.server;

import com.github.zy.netty.rpc.common.protocol.DefaultMessagePacket;
import com.github.zy.netty.rpc.common.protocol.MessageType;
import com.github.zy.netty.rpc.session.Session;
import com.github.zy.netty.rpc.session.SessionHelper;
import com.github.zy.netty.rpc.constants.Profile;
import com.github.zy.netty.rpc.session.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @version 1.0 created by zy on 2020/4/28 0:43
 */
@Slf4j
@Component
public class ReportModuleIdMessageResolver implements ServerMessageResolverStrategy {

    @Value("${spring.profiles.active:dev}")
    private String profile;
    @Autowired
    private SessionManager sessionManager;

    @Override
    public boolean support(MessageType messageType) {
        return MessageType.REPORT_MODULE_ID.equals(messageType);
    }

    @Override
    public void resolver(ChannelHandlerContext ctx, DefaultMessagePacket msg) {
        //处理客户端上报moduleId的策略
        log.info("接收到客户端上报的系统Id请求 systemId: {}, ip : {}", msg.getSystemId(), msg.getTargetIp());
        Session session = SessionHelper.buildServerSession(msg.getTargetIp(), msg.getSystemId(), Profile.get(profile), ctx.channel());
        if (StringUtils.isEmpty(session.getSystemId())) {
            log.error("客户端上报的系统id为空, 客户端ip : {} ", msg.getTargetIp());
            throw new RuntimeException();
        } else {
            sessionManager.save(session);
        }
    }
}
