package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.First;
import com.googlecode.totallylazy.Maps;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.collections.PersistentList;
import com.googlecode.totallylazy.functions.Callables;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.functions.Function2;
import com.googlecode.totallylazy.functions.Unary;
import com.googlecode.totallylazy.predicates.Predicate;
import com.googlecode.totallylazy.time.Dates;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.googlecode.totallylazy.Callers.call;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.functions.Functions.identity;
import static com.googlecode.totallylazy.predicates.Predicates.by;

public abstract class Parameters<Self extends Parameters<Self>> implements Iterable<Pair<String, String>> {
    private final Function1<String, Predicate<String>> predicate;
    protected final PersistentList<Pair<String, String>> values;

    protected Parameters(Function1<String, Predicate<String>> predicate, PersistentList<Pair<String, String>> values) {
        this.predicate = predicate;
        this.values = values;
    }

    protected abstract Self self(PersistentList<Pair<String, String>> values);

    public Self add(String name, String value) {
        return self(values.append(pair(name, value)));
    }

    public Self joinTo(Self self) {
        return self(values.joinTo(self.values));
    }

    public Self remove(String name) {
        return self(values.deleteAll(filterByKey(name)));
    }

    public Self replace(String name, String value) {
        return remove(name).add(name, value);
    }

    public int size() {
        return values.size();
    }

    public String getValue(String key) {
        return valueOption(key).getOrNull();
    }

    public Option<String> valueOption(String key) {
        return filterByKey(key).headOption().map(Callables.<String>second());
    }

    public Sequence<String> getValues(String key) {
        return filterByKey(key).map(Callables.<String>second());
    }

    public boolean contains(String key) {
        return !filterByKey(key).headOption().isEmpty();
    }

    public Iterator<Pair<String, String>> iterator() {
        return values.iterator();
    }

    protected Sequence<Pair<String, String>> filterByKey(String key) {
        Predicate<First<String>> predicate = by(Callables.<String>first(), call(this.predicate, key));
        return filter(predicate).realise();
    }

    public Sequence<Pair<String, String>> filter(final Predicate<First<String>> predicate) {
        return sequence(values).filter(predicate);
    }

    public Option<Pair<String, String>> find(final Predicate<First<String>> predicate) {
        return filter(predicate).headOption();
    }

    public static <Self extends Parameters<Self>> Function2<Self, Pair<String, String>, Self> pairIntoParameters() {
        return (result, pair) -> result.add(pair.first(), pair.second());
    }

    @Override
    public int hashCode() {
        return size();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        if (other instanceof Parameters) {
            final Self parameters = (Self) other;

            if (size() != parameters.size()) return false;

            return sequence(this).zip(parameters).forAll(pairsMatch());
        }
        return false;
    }

    private Predicate<Pair<Pair<String, String>, Pair<String, String>>> pairsMatch() {
        return pair -> {
            Pair<String, String> first = pair.first();
            Pair<String, String> second = pair.second();

            Predicate<String> predicate1 = call(Parameters.this.predicate, first.first());
            return predicate1.matches(second.first()) && first.second().equals(second.second());
        };
    }

    @Override
    public String toString() {
        return sequence(values).toString();
    }

    public Map<String, List<String>> toMap() {
        return Maps.multiMap(this);
    }

    public interface Builder {
        static Unary<Parameters<?>> add(String name, Object value){
            if(value == null) return identity();
            return params -> params.add(name, convert(value));
        }

        static String convert(Object value) {
            if(value instanceof Date) return Dates.RFC822().format((Date) value);
            return value.toString();
        }

        static Unary<Parameters<?>> replace(String name, Object value){
            if(value == null) return remove(name);
            return params -> params.replace(name, convert(value));
        }

        static Unary<Parameters<?>> remove(String name){
            return params -> params.remove(name);
        }

        static Unary<Parameters<?>> param(String name, Object value){
            return replace(name, value);
        }

        static Unary<Parameters<?>> param(String name, List<?> values){
            return params -> sequence(values).fold(params.remove(name), (acc, item) -> acc.add(name, convert(item)));
        }
    }
}
