package com.github.zy.netty.rpc.test;

import com.github.zy.netty.rpc.annotation.RpcMapping;
import com.github.zy.netty.rpc.annotation.RpcService;
import com.github.zy.netty.rpc.test.domain.ClientRequest;
import com.github.zy.netty.rpc.test.domain.ServerResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @version 1.0 created by zy on 2020/5/24 17:38
 */
@Slf4j
@RpcService
public class ServerMessageListener {

    @RpcMapping(url = "/hello-server")
    public ServerResponse serverResponse(ClientRequest request){
        log.info("收到客户端请求数据 ：{}", request);
        ServerResponse response = new ServerResponse();
        response.setId(666);
        response.setDesc("来自服务端的响应~");
        return response;
    }
}
