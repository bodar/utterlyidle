package com.googlecode.utterlyidle.annotations;

@java.lang.annotation.Target({java.lang.annotation.ElementType.ANNOTATION_TYPE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
public @interface HttpMethod {
    java.lang.String GET = "GET";
    java.lang.String POST = "POST";
    java.lang.String PUT = "PUT";
    java.lang.String DELETE = "DELETE";
    java.lang.String HEAD = "HEAD";
    java.lang.String OPTIONS = "OPTIONS";
    java.lang.String ANY = "*";

    java.lang.String value();
}
