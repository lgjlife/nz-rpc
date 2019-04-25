package com.nz.rpc.uid;


import lombok.Data;

@Data
public class UidProperties {

    /**
     *
     * @author lgj
     * @Description UID模式
     *   custom: 自定義
     *   zookeeper: zookeeper
     * @date 4/24/19
    */
    private String type="custom";

    /**
     *功能描述
     * @author lgj
     * @Description  機器ＩＤ
     * @date 4/24/19
    */
    private long macId  = 0;

}
