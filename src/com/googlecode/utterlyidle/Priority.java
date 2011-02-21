package com.googlecode.utterlyidle;

@java.lang.annotation.Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Priority {
    int value();

    static final int High = 10;
    static final int Medium = 0;
    static final int Low = -10;
}
