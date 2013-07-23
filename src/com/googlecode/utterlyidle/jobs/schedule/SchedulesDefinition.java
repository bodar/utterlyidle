package com.googlecode.utterlyidle.jobs.schedule;

import com.googlecode.lazyrecords.Definition;
import com.googlecode.lazyrecords.Keyword;

import java.util.Date;
import java.util.UUID;

import static com.googlecode.lazyrecords.Keyword.constructors.keyword;

public interface SchedulesDefinition extends Definition {
    SchedulesDefinition schedules = constructors.definition(SchedulesDefinition.class, "schedule");
    Keyword<UUID> scheduleId = keyword("schedule_id", UUID.class);
    Keyword<String> request = keyword("request", String.class);
    Keyword<String> response = keyword("response", String.class);
    Keyword<String> start = keyword("start", String.class);
    Keyword<Long> interval = keyword("interval", Long.class);
    Keyword<Long> duration = keyword("duration", Long.class);
    Keyword<Date> started = keyword("started", Date.class);
    Keyword<Date> completed = keyword("completed", Date.class);
    Keyword<Boolean> running = keyword("running", Boolean.class);
}
