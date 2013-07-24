package com.googlecode.utterlyidle.schedules;

import com.googlecode.lazyrecords.Keyword;
import com.googlecode.lazyrecords.Record;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;

import java.util.HashSet;
import java.util.Set;

import static com.googlecode.totallylazy.Sequences.sequence;

public class UniqueRecords implements Predicate<Record> {
    private final Sequence<Keyword<?>> uniqueFields;
    private final Set<Sequence<Object>> uniqueValues = new HashSet<Sequence<Object>>();

    public UniqueRecords(Keyword<?>... uniqueFields) {
        this(sequence(uniqueFields));
    }

    public UniqueRecords(Sequence<Keyword<?>> uniqueFields) {
        this.uniqueFields = uniqueFields;
    }

    public boolean matches(final Record record) {
        Sequence<Object> compoundUniqueKey = record.valuesFor(uniqueFields);
        if(compoundUniqueKey.isEmpty()){
            return true;
        }
        if (uniqueValues.contains(compoundUniqueKey)){
            return false;
        }
        uniqueValues.add(compoundUniqueKey);
        return true;
    }

}
