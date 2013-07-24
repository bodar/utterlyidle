package com.googlecode.utterlyidle.schedules;

import com.googlecode.lazyrecords.Record;
import com.googlecode.lazyrecords.Records;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.jobs.UtterlyIdleRecords;

import java.util.UUID;

import static com.googlecode.lazyrecords.Using.using;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Strings.empty;

public class Schedules {
    private final Records records;

    public Schedules(final UtterlyIdleRecords records) {
        this.records = records.value();
    }

    public void put(Schedule schedule) {
        records.put(SchedulesDefinition.schedules, Record.methods.update(using(SchedulesDefinition.scheduleId), schedule.toRecord()));
    }

    public Sequence<Record> schedules() {
        return records.get(SchedulesDefinition.schedules).realise().filter(where(SchedulesDefinition.request, not(empty()))).filter(new UniqueRecords(SchedulesDefinition.scheduleId));
    }

    public void remove(UUID id) {
        records.remove(SchedulesDefinition.schedules, where(SchedulesDefinition.scheduleId, is(id)));
    }
}