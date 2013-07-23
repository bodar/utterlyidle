package com.googlecode.utterlyidle.jobs.schedule;

import com.googlecode.lazyrecords.Definition;
import com.googlecode.lazyrecords.Record;
import com.googlecode.lazyrecords.Records;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.jobs.UtterlyIdleRecords;

import java.util.UUID;

import static com.googlecode.lazyrecords.Definition.constructors.definition;
import static com.googlecode.lazyrecords.Using.using;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Strings.empty;

public class Schedules {
    private final Records records;
    private static final Definition SCHEDULES = definition("schedule", Schedule.SCHEDULE_ID, Schedule.REQUEST, Schedule.RESPONSE, Schedule.START, Schedule.INTERVAL, Schedule.DURATION, Schedule.STARTED, Schedule.COMPLETED, Schedule.RUNNING);

    public Schedules(final UtterlyIdleRecords records) {
        this.records = records.value();
    }

    public void put(Schedule schedule) {
        records.put(SCHEDULES, Record.methods.update(using(Schedule.SCHEDULE_ID), schedule.toRecord()));
    }

    public Sequence<Record> schedules() {
        return records.get(SCHEDULES).realise().filter(where(Schedule.REQUEST, not(empty()))).filter(new UniqueRecords(Schedule.SCHEDULE_ID));
    }

    public void remove(UUID id) {
        records.remove(SCHEDULES, where(Schedule.SCHEDULE_ID, is(id)));
    }
}