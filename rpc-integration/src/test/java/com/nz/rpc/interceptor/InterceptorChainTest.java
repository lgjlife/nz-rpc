package com.nz.rpc.interceptor;

import com.nz.rpc.invocation.client.ClientInvocation;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InterceptorChainTest {

    InterceptorChain interceptorChain;


    @Before
    public  void before(){
        interceptorChain = new InterceptorChain();
    }


    @Test
    public void addFirst() {

        try{
            for(int i = 0; i< 5; i++){

                interceptorChain.addFirst("name-"+i,new MyInterceptor());

            }
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        System.out.println(interceptorChain.getEntries());

    }

    @Test
    public void addLast() {

        try{
            for(int i = 0; i< 5; i++){

                interceptorChain.addLast("name-"+i,new MyInterceptor());

            }
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        System.out.println(interceptorChain.getEntries());
    }

    @Test
    public void addAfter() {

        try{
            interceptorChain.addFirst("name-0",new MyInterceptor());
            interceptorChain.addBefore("name-0","name-before",new MyInterceptor());
            interceptorChain.addAfter("name-1","name-after",new MyInterceptor());

        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        System.out.println(interceptorChain.getEntries());

    }

    @Test
    public void addBefore() {

    }


}
@Data
class MyInterceptor implements Interceptor{
    @Override
    public Object intercept(ClientInvocation invocation) throws Exception {
        return null;
    }
}