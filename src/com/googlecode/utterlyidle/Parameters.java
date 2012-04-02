package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Callables;
import com.googlecode.totallylazy.Maps;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.googlecode.totallylazy.Callers.call;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Predicates.by;
import static com.googlecode.totallylazy.Sequences.sequence;

public class Parameters<K, V> implements Iterable<Pair<K, V>> {
    private final List<Pair<K, V>> values = new CopyOnWriteArrayList<Pair<K, V>>();
    private final Callable1<K, Predicate<K>> predicate;

    public Parameters(Callable1<K, Predicate<K>> predicate) {
        this.predicate = predicate;
    }

    public Parameters add(K name, V value) {
        values.add(pair(name, value));
        return this;
    }

    public Parameters remove(K name) {
        values.removeAll(filterByKey(name).toList());
        return this;
    }

    public int size() {
        return values.size();
    }

    public V getValue(K key) {
        return valueOption(key).getOrNull();
    }

    public Option<V> valueOption(K key) {
        return filterByKey(key).headOption().map(Callables.<V>second());
    }

    public Sequence<V> getValues(K key) {
        return filterByKey(key).map(Callables.<V>second());
    }

    public boolean contains(K key) {
        return !filterByKey(key).headOption().isEmpty();
    }

    public Iterator<Pair<K, V>> iterator() {
        return values.iterator();
    }

    private Sequence<Pair<K, V>> filterByKey(K key) {
        return sequence(values).filter(by(Callables.<K>first(), call(predicate, key))).realise();
    }

    public static <K, V> Callable2<Parameters<K, V>, Pair<K, V>, Parameters<K, V>> pairIntoParameters() {
        return new Callable2<Parameters<K, V>, Pair<K, V>, Parameters<K, V>>() {
            @SuppressWarnings("unchecked")
            public Parameters<K, V> call(Parameters<K, V> result, Pair<K, V> pair) throws Exception {
                return result.add(pair.first(), pair.second());
            }
        };
    }

    @Override
    public int hashCode() {
        return size();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        if (other instanceof Parameters) {
            final Parameters<K, V> parameters = (Parameters<K, V>) other;

            if (size() != parameters.size()) return false;

            return sequence(this).forAll(sameHeaderExistsIn(parameters));
        }
        return false;
    }

    private Predicate<? super Pair<K, V>> sameHeaderExistsIn(final Parameters<K, V> parameters) {
        return new Predicate<Pair<K, V>>() {
            public boolean matches(Pair<K, V> pair) {
                 return parameters.contains(pair.first()) && parameters.getValue(pair.first()).equals(pair.second());
            }
        };
    }

    @Override
    public String toString() {
        return sequence(values).toString();
    }

    public Map<K, List<V>> toMap() {
        return Maps.multiMap(this);
    }
}
