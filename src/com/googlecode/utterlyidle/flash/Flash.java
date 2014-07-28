package com.googlecode.utterlyidle.flash;

import com.googlecode.funclate.Model;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;

import java.util.List;

import static com.googlecode.totallylazy.Arrays.list;
import static com.googlecode.totallylazy.Pair.pair;

/**
 * Flash is tightly coupled to using Funclate models.
 *
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

    private final Model values = Model.mutable.model();

    public Flash merge(Model value) {
        this.values.merge(value);
        return this;
    }

    public Flash set(String key, Object value) {
        values.set(key, value);
        return this;
    }

    public Flash add(String key, Object value) {
        values.add(key, value);
        return this;
    }

    public <T> Pair<Flash, Option<T>> remove(String key, Class<T> type){
        return remove(key);
    }

    public <T> Pair<Flash, Option<T>> remove(String key){
        Option<T> value = values.<T>remove(key).second();
        return pair(this, value);
    }

    public Flash error(String value) {
        return add(ERRORS, list(value));
    }

    public Flash message(String value) {
        return add(MESSAGES, list(value));
    }

    public List<String> removeErrors(){
        List<String> errors = errors();
        remove(ERRORS);
        return errors;
    }

    public List<String> removeMessages(){
        List<String> messages = messages();
        remove(MESSAGES);
        return messages;
    }

    public List<String> errors() {
        return values.getValues(ERRORS);
    }

    public List<String> messages() {
        return values.getValues(MESSAGES);
    }

    /**
     * Changes to the state() model will not affect the Flash-
     * use the methods on Flash itself for modification
     * @return
     */
    public Model state() {
        return Model.persistent.model(values.pairs());
    }
}
