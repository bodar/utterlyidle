package com.googlecode.utterlyidle.dsl;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.Parameter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ThreadLocalParameters extends ThreadLocal<List<Pair<Type,Option<Parameter>>>> {
    @Override
    public List<Pair<Type, Option<Parameter>>> get() {
        List<Pair<Type, Option<Parameter>>> pairs = super.get();
        if(pairs == null){
            List<Pair<Type, Option<Parameter>>> list = new ArrayList<Pair<Type, Option<Parameter>>>();
            set(list);
            return list;
        }
        return pairs;
    }

    @Override
    public void set(List<Pair<Type, Option<Parameter>>> value) {
        super.set(value);
    }
}
