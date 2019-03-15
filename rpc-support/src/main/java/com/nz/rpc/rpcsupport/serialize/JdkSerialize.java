package com.nz.rpc.rpcsupport.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述
 *
 * @author lgj
 * @Description JDK序列化方式
 * @date 1/24/19
 */
public class JdkSerialize implements Serialize {


    @Override
    public Object deserialize(byte[] b) {
        ByteArrayInputStream bis = new ByteArrayInputStream(b);

        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object obj = ois.readObject();
            return obj;
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return null;
    }

    @Override
    public Object deserialize(byte[] b, Class clazz) {
        return null;
    }

    @Override
    public byte[] serialize(Object obj) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            oos.writeObject(obj);
            return bos.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new byte[0];
    }


    public static void main(String args[]) {
        User user1 = new User("lgj", 12);

        JdkSerialize jdkSerialize = new JdkSerialize();
        byte[] bObj = jdkSerialize.serialize(user1);

        User user2 = (User) jdkSerialize.deserialize(bObj);

        System.out.println(user2);


        System.out.println("--------------------------");
        List<User> ul1 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ul1.add(new User("liang", i));
        }


        byte[] uj1 = jdkSerialize.serialize(ul1);
        List<User> ul2 = (List) jdkSerialize.deserialize(uj1);

        ul2.forEach((data) -> System.out.println(data));


    }
}