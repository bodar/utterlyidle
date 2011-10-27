package com.googlecode.utterlyidle.annotations;

@java.lang.annotation.Inherited
@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.METHOD})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Consumes {

    java.lang.String[] value() default {"*/*"};
}
