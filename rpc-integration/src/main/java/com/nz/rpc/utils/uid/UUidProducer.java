package com.nz.rpc.utils.uid;

import java.util.UUID;

public class UUidProducer implements UidProducer {

    @Override
    public String getUid() {

        //FFFF FFFF 0000 0000
        return UUID.randomUUID().toString();
    }
}
