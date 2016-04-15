package com.googlecode.utterlyidle.aws;

import com.googlecode.totallylazy.time.Dates;
import org.junit.Test;

import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.utterlyidle.aws.AwsRequestDate.awsRequestDate;
import static org.junit.Assert.assertThat;

public class AwsRequestDateTest {

    @Test
    public void basic_representation() {
        assertThat(awsRequestDate(Dates.date(2016, 12, 25, 7, 35, 49)).basic(), is("20161225"));
    }

    @Test
    public void full_representation() {
        assertThat(awsRequestDate(Dates.date(2016, 12, 25, 7, 35, 49, 123)).full(), is("20161225T073549Z"));
    }

}