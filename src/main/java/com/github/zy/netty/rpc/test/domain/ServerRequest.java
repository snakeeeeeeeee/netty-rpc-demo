package com.github.zy.netty.rpc.test.domain;

import lombok.Data;

/**
 * @version 1.0 created by zy on 2020/5/24 17:27
 */
@Data
public class ServerRequest {

    private String systemId;

    private String ip;

    private String urlMapping;

    private String desc;
}
