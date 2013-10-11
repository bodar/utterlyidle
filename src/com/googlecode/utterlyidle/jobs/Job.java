package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.Mapper;
import com.googlecode.totallylazy.Option;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.util.Date;
import java.util.UUID;

public interface Job {
    UUID id();
    Request request();
    Option<Response> response();
    Date created();
    Option<Date> started();
    Option<Date> completed();

    class functions {
        public static Mapper<Job, Date> started() {
            return new Mapper<Job, Date>() {
                @Override
                public Date call(Job job) throws Exception {
                    return job.started().get();
                }
            };
        }

        public static Mapper<Job, Date> completed() {
            return new Mapper<Job, Date>() {
                @Override
                public Date call(Job job) throws Exception {
                    return job.completed().get();
                }
            };
        }
    }
}
