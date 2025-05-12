package com.onyell.batataquente.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Commands {

    String cmd() default "";
    String[] alias() default {};
    boolean onlyPlayer() default false;
}