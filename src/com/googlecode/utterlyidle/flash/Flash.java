package com.googlecode.utterlyidle.flash;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Unchecked;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.googlecode.totallylazy.Arrays.list;
import static com.googlecode.totallylazy.Maps.pairs;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Unchecked.cast;

/**
 * Calling flash.state().get(key) violates the law of Demeter,
 * since state() is immutable, this only creates coupling, not
 * confusing update problems.
 *
 * This is a deliberate design decision. If you require a different
 * implementation of flash messaging, you are urged to copy and
 * modify this code.
 */
public class Flash {
    public static final String ERRORS = "errors";
    public static final String MESSAGES = "messages";

    private final Map<String,Object> values = new HashMap<>();

    public Flash merge(Map<String,Object> value) {
        merge(values, value);
        return this;
    }

    public static Map<String,Object> merge(Map<String,Object> seed, Map<String, Object> additions){
        return merge(seed, pairs(additions));
    }

    @SafeVarargs
    public static Map<String,Object> merge(Map<String,Object> seed, Pair<String, Object>... additions){
        return merge(seed, sequence(additions));
    }

    public static Map<String,Object> merge(Map<String,Object> seed, Iterable<? extends Pair<String, Object>> additions){
        for (Pair<String, Object> pair : additions) {
            Object value = seed.get(pair.first());
            if(value instanceof List) {
                Unchecked.<List<Object>>cast(value).add(pair.second());
                continue;
            }
            if(value != null) {
                seed.put(pair.first(), list(value, pair.second()));
                continue;
            }
            seed.put(pair.first(), pair.second());
        }
        return seed;
    }

    public Flash set(String key, Object value) {
        values.put(key, value);
        return this;
    }

    public Flash add(String key, Object value) {
        merge(values, pair(key, value));
        return this;
    }

    public <T> Option<T> remove(String key, Class<T> type){
        return remove(key);
    }

    public <T> Option<T> remove(String key){
        return Option.<T>option(cast(values.remove(key)));
    }

    public Flash error(String value) {
        return add(ERRORS, value);
    }

    public Flash message(String value) {
        return add(MESSAGES, value);
    }

    public List<String> removeErrors(){
        return this.<List<String>>remove(ERRORS).get();
    }

    public List<String> removeMessages(){
        return this.<List<String>>remove(MESSAGES).get();
    }

    public List<String> errors() {
        return lists(ERRORS);
    }

    public List<String> messages() {
        return lists(MESSAGES);
    }

    private List<String> lists(final String key) {
        Object result = values.get(key);
        if(result instanceof List) return cast(result);
        return list();
    }

    /**
     * Changes to the state() model will not affect the Flash-
     * use the methods on Flash itself for modification
     * @return
     */
    public Map<String, Object> state() {
        return Collections.unmodifiableMap(values);
    }
}
