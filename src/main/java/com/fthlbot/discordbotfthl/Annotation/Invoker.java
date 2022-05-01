package com.fthlbot.discordbotfthl.Annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Invoker {
    String alias();
    String description();
    String usage();
    CommandType type() default CommandType.MISC;

    AllowedChannel where() default AllowedChannel.ANYWHERE;
}
