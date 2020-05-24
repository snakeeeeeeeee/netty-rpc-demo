package com.github.zy.netty.rpc.service;

import com.github.zy.netty.rpc.event.CleanNotActiveSessionEvent;
import com.github.zy.netty.rpc.event.CleanSessionEventMessage;
import com.github.zy.netty.rpc.session.SessionStorage;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0 created by zy on 2020/5/8 11:30
 */
@Slf4j
@RequiredArgsConstructor
public class CleanSessionService {

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final SessionStorage sessionStorage;
    private final ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        clearSession();
    }

    public void clearSession() {
        executor.scheduleAtFixedRate(() -> {
            log.debug("检查非活动状态session....");
            List<String> removeIds = new ArrayList<>();
            List<CleanSessionEventMessage> messages = new ArrayList<>();
            sessionStorage.findAll().forEach(session -> {
                Channel channel = session.getChannel();
                boolean active = channel.isActive();
                if (!active) {
                    removeIds.add(session.getId());
                    messages.add(CleanSessionEventMessage.builder().sessionId(session.getId()).systemId(session.getSystemId()).ip(session.getIp()).build());
                }
            });

            //移除不是活动中的session
            if (!CollectionUtils.isEmpty(removeIds)) {
                log.debug("移除非活动状态session {}", removeIds);
                sessionStorage.delete(removeIds);


            }

            if (!CollectionUtils.isEmpty(messages)) {
                log.debug("发布移除session事件...");
                //发布清除session事件
                applicationContext.publishEvent(new CleanNotActiveSessionEvent(this, messages));
            }

        }, 5, 5, TimeUnit.SECONDS);
    }


}
