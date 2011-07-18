package com.googlecode.utterlyidle.annotations;

import com.googlecode.totallylazy.Value;

@java.lang.annotation.Target({java.lang.annotation.ElementType.PARAMETER, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface CookieParam {
    java.lang.String value();
}
