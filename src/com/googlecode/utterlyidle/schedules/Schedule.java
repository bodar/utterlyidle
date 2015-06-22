package com.googlecode.utterlyidle.schedules;

import com.googlecode.lazyrecords.Keyword;
import com.googlecode.lazyrecords.Record;
import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.time.Clock;

import java.util.Date;
import java.util.UUID;

import static com.googlecode.lazyrecords.Record.constructors.record;
import static com.googlecode.utterlyidle.schedules.NextTime.nextTime;
import static com.googlecode.utterlyidle.schedules.SchedulesDefinition.scheduleId;

public class Schedule {
    private final Record record;

    private Schedule(Record record) {
        this.record = record;
    }

    public static Schedule schedule(UUID id) {
        return new Schedule(record().set(scheduleId, id));
    }

    public Schedule interval(Long interval) {
        return set(SchedulesDefinition.interval, interval);
    }

    public Schedule request(String request) {
        return set(SchedulesDefinition.request, request);
    }

    public Schedule response(String response) {
        return set(SchedulesDefinition.response, response);
    }

    public Schedule started(Date started) {
        return set(SchedulesDefinition.started, started);
    }

    public Schedule completed(Date completed) {
        return set(SchedulesDefinition.completed, completed);
    }

    public Schedule start(String start) {
        return set(SchedulesDefinition.start, start);
    }

    public static Date start(String time, Clock clock) {
        return nextTime(time, clock).value();
    }

    public Schedule running(boolean running) {
        return set(SchedulesDefinition.running, running);
    }

    public Schedule duration(Long duration) {
        return set(SchedulesDefinition.duration, duration);
    }

    public Record toRecord() {
        return record;
    }

    public <T> T get(Keyword<T> keyword) {
        return record.get(keyword);
    }

    public static Function1<Record, UUID> asScheduleId() {
        return record1 -> record1.get(scheduleId);
    }

    private <T> Schedule set(Keyword<T> keyword, T value) {
        return new Schedule(record.set(keyword, value));
    }

    @Override
    public int hashCode() {
        return record.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Schedule schedule = (Schedule) o;

        return record.equals(schedule.record);
    }

    @Override
    public String toString() {
        return record.toString();
    }
}
