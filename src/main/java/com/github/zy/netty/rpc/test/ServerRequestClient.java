package com.github.zy.netty.rpc.test;

import com.github.zy.netty.rpc.annotation.RpcMapping;
import com.github.zy.netty.rpc.annotation.RpcRequestClient;
import com.github.zy.netty.rpc.test.domain.ClientRequest;
import com.github.zy.netty.rpc.test.domain.ServerResponse;

/**
 * @version 1.0 created by zy on 2020/5/24 17:37
 */
@RpcRequestClient
public interface ServerRequestClient {

    @RpcMapping(url = "/hello-server")
    ServerResponse helloServer(ClientRequest request);
}
