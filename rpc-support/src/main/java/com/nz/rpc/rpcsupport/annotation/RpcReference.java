package com.nz.rpc.rpcsupport.annotation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Indexed
@Autowired(required = false)
public @interface RpcReference {

    Class<?> interfaceClass() default void.class;
}
