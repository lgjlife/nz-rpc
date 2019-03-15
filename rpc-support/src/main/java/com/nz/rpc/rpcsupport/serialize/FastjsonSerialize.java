package com.nz.rpc.rpcsupport.serialize;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述
 *
 * @author lgj
 * @Description JDK序列化方式
 * @date 1/24/19
 */

@Slf4j
public class FastjsonSerialize implements Serialize {


    @Override
    public Object deserialize(byte[] b) {
        log.info("length = " + b.length);
        Object obj = (Object) JSON.parseObject(b, User.class);
        return obj;
    }

    @Override
    public byte[] serialize(Object obj) {
        String str = JSON.toJSONString(obj);
        log.info("str = " + str);
        return str.getBytes();
    }

    @Override
    public Object deserialize(byte[] b, Class clazz) {
        log.info("length = " + b.length);

        Object obj = (Object) JSON.parseObject(b, clazz);
        return obj;
    }

    public <T> Object deserialize1(byte[] b, Class clazz) {
        log.info("length = " + b.length);

        Object obj = (Object) JSON.parseObject(b, clazz);
        return obj;
    }


    public static void main(String args[]) {

        User user = new User("dmego", 1);
        System.out.println(user);
        String UserJson = JSON.toJSONString(user, false);
        System.out.println("简单java类转json字符串:" + UserJson);

        String txt = "{\"name\":\"dmego\",\"age\":1}";
        User user1 = JSON.parseObject(txt, new TypeReference<User>() {
        });
        System.out.println(user1);
        //  System.out.println("len = " + user1.size());
        // System.out.println(user1.get(0));


        User user2 = new User("lgj", 12);

        FastjsonSerialize serialize = new FastjsonSerialize();

        byte[] bObj = serialize.serialize(user2);

        User user3 = (User) serialize.deserialize(bObj);

        System.out.println(user3);


        System.out.println("--------------------------");
        User uu1 = new User("dmego", 1);
        String uu1Str = JSON.toJSONString(uu1);
        Object obj = JSON.parseObject(uu1Str);

        User uu2 = JSON.parseObject(uu1Str, new TypeReference<User>() {
        });
        System.out.println(obj);
        System.out.println(uu2);
        //  User uu2 = ((JSONObject) obj).getObject(,User.class)

        System.out.println("--------------------------");
        List<User> ul1 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ul1.add(new User("liang", i));
        }

        Serialize serialize1 = new FastjsonSerialize();
        byte[] uj1 = serialize.serialize(ul1);
        List<User> ul2 = (List) serialize1.deserialize(uj1, List.class);

        // ul2.forEach((data)-> System.out.println(data));

        User u = ul2.get(1);
        System.out.println(u);
    }
}


