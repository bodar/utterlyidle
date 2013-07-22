package com.googlecode.utterlyidle.schedule;

import com.googlecode.lazyrecords.Keyword;
import com.googlecode.lazyrecords.Record;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.time.Clock;

import java.util.Date;
import java.util.UUID;

import static com.googlecode.lazyrecords.Keyword.constructors.keyword;
import static com.googlecode.lazyrecords.Record.constructors.record;
import static com.googlecode.utterlyidle.schedule.NextTime.nextTime;

public class Job {
    public static final Keyword<UUID> JOB_ID = keyword("jobs_id", UUID.class);
    public static final Keyword<String> REQUEST = keyword("request", String.class);
    public static final Keyword<String> RESPONSE = keyword("response", String.class);
    public static final Keyword<String> START = keyword("start", String.class);
    public static final Keyword<Long> INTERVAL = keyword("interval", Long.class);
    public static final Keyword<Long> DURATION = keyword("duration", Long.class);
    public static final Keyword<Date> STARTED = keyword("started", Date.class);
    public static final Keyword<Date> COMPLETED = keyword("completed", Date.class);
    public static final Keyword<Boolean> RUNNING = keyword("running", Boolean.class);

    private final Record record;

    private Job(Record record) {
        this.record = record;
    }

    public static Job job(UUID id) {
        return new Job(record().set(JOB_ID, id));
    }

    public Job interval(Long interval) {
        return set(INTERVAL, interval);
    }

    public Job request(String request) {
        return set(REQUEST, request);
    }

    public Job response(String response) {
        return set(RESPONSE, response);
    }

    public Job started(Date started) {
        return set(STARTED, started);
    }

    public Job completed(Date completed) {
        return set(COMPLETED, completed);
    }

    public Job start(String start) {
        return set(START, start);
    }

    public static Date start(String time, Clock clock) {
        return nextTime(time, clock).value();
    }

    public Job running(boolean running) {
        return set(RUNNING, running);
    }

    public Job duration(Long duration) {
        return set(DURATION, duration);
    }

    public Record toRecord() {
        return record;
    }

    public <T> T get(Keyword<T> keyword) {
        return record.get(keyword);
    }

    public static Callable1<Record, UUID> asJobId() {
        return new Callable1<Record, UUID>() {
            public UUID call(Record record) throws Exception {
                return record.get(JOB_ID);
            }
        };
    }

    private <T> Job set(Keyword<T> keyword, T value) {
        return new Job(record.set(keyword, value));
    }
}
