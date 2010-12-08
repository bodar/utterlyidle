package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callables;
import com.googlecode.totallylazy.Maps;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.Sets;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.googlecode.totallylazy.Sequences.sequence;

public class PropertyMap extends AbstractMap<String, Object> {
    private final Property property;

    public PropertyMap(Property property) {
        this.property = property;
    }

    @Override
    public String toString() {
        return property.getValue();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return Sets.set(sequence(property.getChildren()).map(asEntry()));
    }

    private Callable1<? super Property, Entry<String, Object>> asEntry() {
        return new Callable1<Property, Entry<String, Object>>() {
            public Entry<String, Object> call(final Property property) throws Exception {
                return new PropertyEntry(property);
            }
        };
    }

    private static class PropertyEntry implements Entry<String, Object> {
        private final Property property;

        public PropertyEntry(Property property) {
            this.property = property;
        }

        public String getKey() {
            return property.getName();
        }

        public Object getValue() {
            return property.getValue();
        }

        public Object setValue(Object value) {
            throw new UnsupportedOperationException();
        }
    }
}
