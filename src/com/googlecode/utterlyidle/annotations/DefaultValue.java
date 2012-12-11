package com.googlecode.utterlyidle.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({PARAMETER, METHOD, FIELD})
@Retention(RUNTIME)
public @interface DefaultValue {
    java.lang.String value();
}