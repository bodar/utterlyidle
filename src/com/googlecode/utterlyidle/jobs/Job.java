package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.util.Date;
import java.util.UUID;

import static com.googlecode.totallylazy.Option.applicate;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.time.Seconds.functions.between;

public interface Job {
    String status();

    UUID id();

    Request request();

    Option<Response> response();

    Date created();

    Option<Date> started();

    Option<Date> completed();

    class functions {
        public static Function1<Job, UUID> id = new Function1<Job, UUID>() {
            @Override
            public UUID call(final Job job) throws Exception {
                return job.id();
            }
        };

        public static Function1<Job, Date> created = new Function1<Job, Date>() {
            @Override
            public Date call(Job job) throws Exception {
                return job.created();
            }
        };

        public static Function1<Job, Option<Date>> started = new Function1<Job, Option<Date>>() {
            @Override
            public Option<Date> call(Job job) throws Exception {
                return job.started();
            }
        };

        public static Function1<Job, Option<Date>> completed = new Function1<Job, Option<Date>>() {
            @Override
            public Option<Date> call(Job job) throws Exception {
                return job.completed();
            }
        };

        public static Function1<Job, Request> request = new Function1<Job, Request>() {
            @Override
            public Request call(final Job job) throws Exception {
                return job.request();
            }
        };

        public static Function1<Job, Option<Response>> response = new Function1<Job, Option<Response>>() {
            @Override
            public Option<Response> call(final Job job) throws Exception {
                return job.response();
            }
        };
    }

    class methods {
        public static Option<Long> duration(Job job, Clock clock){
            return applicate(applicate(some(between), job.started()), some(job.completed().getOrElse(clock.now())));
        }
    }
}
