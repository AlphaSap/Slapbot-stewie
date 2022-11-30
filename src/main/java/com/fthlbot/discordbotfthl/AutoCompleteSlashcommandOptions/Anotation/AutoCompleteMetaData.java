package com.fthlbot.discordbotfthl.AutoCompleteSlashcommandOptions.Anotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AutoCompleteMetaData {
    String optionName();
}
