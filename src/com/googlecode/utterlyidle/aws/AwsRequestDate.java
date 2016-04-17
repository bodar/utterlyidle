package com.googlecode.utterlyidle.aws;

import com.googlecode.totallylazy.Value;
import com.googlecode.totallylazy.time.Dates;

import java.util.Date;

public class AwsRequestDate implements Value<Date> {
    private final Date date;

    private AwsRequestDate(Date date) {
        this.date = date;
    }

    public static AwsRequestDate awsRequestDate(Date date) {
        return new AwsRequestDate(date);
    }

    public String basic() {
        return Dates.format("yyyyMMdd").format(date);
    }

    public String full() {
        return Dates.format("yyyyMMdd'T'HHmmss'Z'").format(date);
    }

    @Override
    public Date value() {
        return date;
    }
}
