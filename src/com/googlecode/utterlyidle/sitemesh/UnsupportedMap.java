package com.googlecode.utterlyidle.sitemesh;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class UnsupportedMap<K,V> implements Map<K,V> {
    public int size() {
        throw blow();
    }

    private UnsupportedOperationException blow() {
        return new UnsupportedOperationException(currentMethodName());
    }

    public boolean isEmpty() {
        throw blow();
    }

    public boolean containsKey(Object key) {
        throw blow();
    }

    public boolean containsValue(Object value) {
        throw blow();
    }

    public V get(Object key) {
        throw blow();
    }

    public V put(K key, V value) {
        throw blow();
    }

    public V remove(Object key) {
        throw blow();
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        throw blow();
    }

    public void clear() {
        throw blow();
    }

    public Set<K> keySet() {
        throw blow();
    }

    public Collection<V> values() {
        throw blow();
    }

    public Set<Entry<K, V>> entrySet() {
        throw blow();
    }

    public String currentMethodName() {
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
        System.out.println(methodName);
        return methodName;
    }
}
