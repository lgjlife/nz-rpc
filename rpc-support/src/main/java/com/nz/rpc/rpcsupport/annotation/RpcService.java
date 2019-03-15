package com.nz.rpc.rpcsupport.annotation;


import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RpcService {

    Class<?> interfaceClass() default void.class;
}
