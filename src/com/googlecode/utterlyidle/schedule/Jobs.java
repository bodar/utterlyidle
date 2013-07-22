package com.googlecode.utterlyidle.schedule;

import com.googlecode.lazyrecords.Definition;
import com.googlecode.lazyrecords.Record;
import com.googlecode.lazyrecords.Records;
import com.googlecode.totallylazy.Sequence;

import java.util.UUID;

import static com.googlecode.lazyrecords.Using.using;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Strings.empty;

public class Jobs {
    private final Records records;
    private static final Definition JOBS = Definition.constructors.definition("jobs", Job.JOB_ID, Job.REQUEST, Job.RESPONSE, Job.START, Job.INTERVAL, Job.DURATION, Job.STARTED, Job.COMPLETED, Job.RUNNING);

    public Jobs(final Records records) {
        this.records = records;
    }

    public void createOrUpdate(Job job) {
        records.put(JOBS, Record.methods.update(using(Job.JOB_ID), job.toRecord()));
    }

    public Sequence<Record> jobs() {
        return records.get(JOBS).realise().filter(where(Job.REQUEST, not(empty()))).filter(new UniqueRecords(Job.JOB_ID));
    }

    public void remove(UUID id) {
        records.remove(JOBS, where(Job.JOB_ID, is(id)));
    }
}