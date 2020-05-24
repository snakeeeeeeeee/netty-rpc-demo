package com.github.zy.netty.rpc.proxy;

import com.github.zy.netty.rpc.constants.RpcRequestClientType;
import lombok.Builder;
import lombok.Data;

/**
 * @version 1.0 created by zy on 2020/4/26 10:26
 */
@Data
@Builder
public class RpcRequestTemplate {

    private String urlMapping;

    /**
     * 模块Id
     */
    private String systemId;

    /**
     * 请求类型, 用来区分请请求
     */
    private RpcRequestClientType type;

    /**
     * 消息载荷
     */
    private String payload;

}
