package com.googlecode.utterlyidle.annotations;

import com.googlecode.totallylazy.Value;
import com.googlecode.totallylazy.annotations.AnnotationLiteral;

@java.lang.annotation.Target(java.lang.annotation.ElementType.METHOD)
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface View {
    String value();

    class constructors {
        public static View view(final String value) {
            return new Instance(value);
        }
    }

    class Instance extends AnnotationLiteral<View> implements View, Value<String> {
        private final String value;

        public Instance(final String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }
    }
}