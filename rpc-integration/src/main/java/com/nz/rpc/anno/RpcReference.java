package com.nz.rpc.anno;


import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Indexed
public @interface RpcReference {

    Class<?> interfaceClass() default void.class;
}
