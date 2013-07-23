package com.googlecode.utterlyidle.jobs.schedule;

import com.googlecode.lazyrecords.Record;
import com.googlecode.totallylazy.Block;
import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.totallylazy.time.Seconds;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;
import com.googlecode.utterlyidle.Status;
import com.googlecode.utterlyidle.rendering.ExceptionRenderer;
import com.googlecode.utterlyidle.services.Service;
import com.googlecode.yadic.Container;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Option.option;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Strings.isEmpty;
import static com.googlecode.utterlyidle.HttpMessageParser.parseRequest;

public class HttpScheduler implements Service {
    private final Schedules schedules;
    private final Scheduler scheduler;
    private final Application application;
    private final Clock clock;

    public HttpScheduler(final Schedules schedules, final Scheduler scheduler, final Application application, final Clock clock) {
        this.schedules = schedules;
        this.scheduler = scheduler;
        this.application = application;
        this.clock = clock;
    }

    public UUID schedule(Schedule schedule) {
        schedules.put(schedule);

        UUID id = schedule.get(SchedulesDefinition.scheduleId);
        schedule(schedule(id).get());
        return id;
    }

    public void start() {
        schedules().each(schedule());
    }

    public void stop() {
        try {
            schedules().each(cancel());
        } catch (Exception ignored) {
        }
    }

    public void remove(UUID id) {
        scheduler.cancel(id);
        schedules.remove(id);
    }

    public Sequence<Record> schedules() {
        return schedules.schedules();
    }

    public Option<Record> schedule(UUID id) {
        return schedules().find(where(SchedulesDefinition.scheduleId, is(id)));
    }

    private Callable<Void> httpTask(final UUID id, final Application application, final Request request) {
        return new Callable<Void>() {
            public Void call() throws Exception {
                final Date started = clock.now();
                try {
                    application.usingRequestScope(update(
                            Schedule.schedule(id).response(null).started(started).completed(null).running(true)));
                    final Response response = application.handle(request);
                    Date completed = clock.now();
                    return application.usingRequestScope(update(
                            Schedule.schedule(id).response(response.toString()).duration(Seconds.between(started, completed)).completed(completed).running(false)));
                } catch (Exception e) {
                    Date completed = clock.now();
                    return application.usingRequestScope(update(
                            Schedule.schedule(id).response(ResponseBuilder.response(Status.INTERNAL_SERVER_ERROR).
                                    entity(ExceptionRenderer.toString(e)).toString()).
                                    duration(Seconds.between(started, completed)).
                                    completed(completed).running(false)));
                }
            }
        };
    }

    private Block<Container> update(final Schedule schedule) {
        return new Block<Container>() {
            public void execute(Container container) throws Exception {
                Schedules newTransaction = container.get(Schedules.class);
                newTransaction.put(schedule);
            }
        };
    }

    private Block<Record> cancel() {
        return new Block<Record>() {
            @Override
            protected void execute(Record record) throws Exception {
                scheduler.cancel(record.get(SchedulesDefinition.scheduleId));
            }
        };
    }

    private Block<Record> schedule() {
        return new Block<Record>() {
            @Override
            protected void execute(Record record) throws Exception {
                HttpScheduler.this.schedule(record);
            }
        };
    }

    private void schedule(Record record) {
        Option<String> start = option(isEmpty(record.get(SchedulesDefinition.start)) ? null : record.get(SchedulesDefinition.start));
        scheduler.schedule(record.get(SchedulesDefinition.scheduleId), httpTask(record.get(SchedulesDefinition.scheduleId), application, parseRequest(record.get(SchedulesDefinition.request))), start.map(toStart()), record.get(SchedulesDefinition.interval));
    }

    private Function1<String, Date> toStart() {
        return new Function1<String, Date>() {
            @Override
            public Date call(String time) throws Exception {
                return Schedule.start(time, clock);
            }
        };
    }
}
