package com.github.zy.netty.rpc.common.serialize;

import com.alibaba.fastjson.JSON;

/**
 * @version 1.0 created by zy on 2020/4/23 14:32
 */
public interface Serializer {
    byte[] serialize(Object object);

    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
