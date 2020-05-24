package com.github.zy.netty.rpc.domain;

import lombok.Data;

/**
 * @version 1.0 created by zy on 2020/4/28 18:01
 */
@Data
public class MessageResponseModule {

    private Integer code;

    private String info;

    private String payload;
}
