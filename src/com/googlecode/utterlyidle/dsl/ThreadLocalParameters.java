package com.googlecode.utterlyidle.dsl;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.NamedParameter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ThreadLocalParameters extends ThreadLocal<List<Pair<Type,Option<NamedParameter>>>> {
    @Override
    public List<Pair<Type, Option<NamedParameter>>> get() {
        List<Pair<Type, Option<NamedParameter>>> pairs = super.get();
        if(pairs == null){
            List<Pair<Type, Option<NamedParameter>>> list = new ArrayList<Pair<Type, Option<NamedParameter>>>();
            set(list);
            return list;
        }
        return pairs;
    }

    @Override
    public void set(List<Pair<Type, Option<NamedParameter>>> value) {
        super.set(value);
    }
}
