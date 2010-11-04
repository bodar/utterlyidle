package com.googlecode.utterlyidle.rendering;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Option;

import static com.googlecode.utterlyidle.rendering.Model.model;

public class Models {
    public static Callable2<Model, String, Model> handleInvalidValue(final String fieldName, final String message) {
        return new Callable2<Model, String, Model>() {
            public Model call(Model model, String invalidValue) throws Exception {
                return model.add("field", model().
                        add("name", fieldName).
                        add("value", invalidValue).
                        add("message", message));
            }
        };
    }

    public static <T> Callable2<Model, Option<T>, Model> handleOptional(final Callable2<Model, T, Model> callable) {
        return new Callable2<Model, Option<T>, Model>() {
            public Model call(Model model, Option<T> optional) throws Exception {
                return optional.fold(model, callable);
            }
        };
    }


}
