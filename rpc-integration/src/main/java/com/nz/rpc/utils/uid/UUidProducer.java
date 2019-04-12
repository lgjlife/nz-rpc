package com.nz.rpc.utils.uid;

import java.util.UUID;

public class UUidProducer implements UidProducer {

    @Override
    public String getUid() {
        return UUID.randomUUID().toString();
    }
}
