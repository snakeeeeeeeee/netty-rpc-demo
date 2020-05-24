package com.github.zy.netty.rpc.test;

import com.github.zy.netty.rpc.annotation.RpcClient;
import com.github.zy.netty.rpc.annotation.RpcMapping;
import com.github.zy.netty.rpc.test.domain.ClientResponse;
import com.github.zy.netty.rpc.test.domain.ServerRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @version 1.0 created by zy on 2020/5/24 18:09
 */
@Slf4j
@RpcClient
public class ClientMessageListener {

    @RpcMapping(url = "/hello-client")
    public ClientResponse clientResponse(ServerRequest request){
        log.info("接收到来自服务端的请求 ：{}", request);
        ClientResponse response = new ClientResponse();
        response.setDesc("你好服务端~");
        return response;
    }
}
