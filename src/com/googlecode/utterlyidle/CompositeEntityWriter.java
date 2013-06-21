package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Block;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Rules;

import java.io.OutputStream;
import java.util.Set;

import static com.googlecode.totallylazy.Unchecked.cast;
import static com.googlecode.utterlyidle.EntityWriter.functions.asFunction;

public class CompositeEntityWriter implements EntityWriter<Object> {
    private final Rules<Object,Block<OutputStream>> rules = Rules.rules();

    @Override
    public void write(Object entity, OutputStream outputStream) throws Exception {
        rules.call(entity).call(outputStream);
    }

    public <T> void add(Predicate<? super T> predicate, final EntityWriter<? super T> writer) {
        Predicate<Object> cast = cast(predicate);
        EntityWriter<Object> objectWriter = cast(writer);
        rules.addFirst(cast, asFunction(objectWriter));
    }
}
