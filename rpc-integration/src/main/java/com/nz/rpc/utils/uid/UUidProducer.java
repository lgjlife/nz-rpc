package com.nz.rpc.utils.uid;

import java.util.UUID;

public class UUidProducer implements UidProducer {

    @Override
    public String getUidForString() {

        //FFFF FFFF 0000 0000
        return UUID.randomUUID().toString();
    }



    @Override
    public long getUidForLong() {
        throw  new UnsupportedOperationException();
    }
}
