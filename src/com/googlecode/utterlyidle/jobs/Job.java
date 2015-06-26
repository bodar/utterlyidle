package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.functions.Function1;
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
        public static Function1<Job, UUID> id = Job::id;

        public static Function1<Job, Date> created = Job::created;

        public static Function1<Job, Option<Date>> started = Job::started;

        public static Function1<Job, Option<Date>> completed = Job::completed;

        public static Function1<Job, Request> request = Job::request;

        public static Function1<Job, Option<Response>> response = Job::response;
    }

    class methods {
        public static Option<Long> duration(Job job, Clock clock){
            return applicate(applicate(some(between), job.started()), some(job.completed().getOrElse(clock.now())));
        }
    }
}
