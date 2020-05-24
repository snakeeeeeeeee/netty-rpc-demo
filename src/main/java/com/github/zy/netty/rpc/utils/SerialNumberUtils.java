package com.github.zy.netty.rpc.utils;

import java.util.UUID;


public enum SerialNumberUtils {

    INSTANCE;

    public String generateSerialNumber() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
