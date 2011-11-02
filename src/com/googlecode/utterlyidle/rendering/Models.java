package com.googlecode.utterlyidle.rendering;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Option;

public class Models {

    public static <T> Callable2<Model, Option<T>, Model> handleOptional(final Callable2<Model, T, Model> callable) {
        return new Callable2<Model, Option<T>, Model>() {
            public Model call(Model model, Option<T> optional) throws Exception {
                return optional.fold(model, callable);
            }
        };
    }


}
