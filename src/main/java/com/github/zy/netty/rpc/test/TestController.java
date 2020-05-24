package com.github.zy.netty.rpc.test;

import com.github.zy.netty.rpc.service.ServerSendTemplate;
import com.github.zy.netty.rpc.session.Session;
import com.github.zy.netty.rpc.session.SessionManager;
import com.github.zy.netty.rpc.test.domain.ClientRequest;
import com.github.zy.netty.rpc.test.domain.ClientResponse;
import com.github.zy.netty.rpc.test.domain.ServerRequest;
import com.github.zy.netty.rpc.test.domain.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @version 1.0 created by zy on 2020/5/24 17:21
 */
@RestController
public class TestController {

    @Autowired
    private ServerSendTemplate serverSendTemplate;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private ServerRequestClient serverRequestClient;

    @GetMapping("/hello-client")
    public ClientResponse helloClient(ServerRequest serverRequest) {
        return serverSendTemplate.sendToClient(serverRequest.getSystemId(), serverRequest.getIp(), serverRequest.getUrlMapping(), serverRequest, ClientResponse.class);
    }

    @GetMapping("/hello-server")
    public ServerResponse helloServer(ClientRequest clientRequest) {
        return serverRequestClient.helloServer(clientRequest);
    }

    @GetMapping("/sessions")
    public List<Session> sessions() {
        return sessionManager.findAll();
    }
}
