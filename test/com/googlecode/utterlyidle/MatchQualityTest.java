package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.annotations.PathParam;
import com.googlecode.utterlyidle.annotations.Priority;
import org.junit.Test;

import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.utterlyidle.ApplicationBuilder.application;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static org.hamcrest.MatcherAssert.assertThat;

public class MatchQualityTest {
    @Test
    public void matchesLongestPathsBeforePriority() throws Exception {
        assertThat(application().addAnnotated(User.class).responseAsString(get("user/dan")), is("Hello dan"));
    }

    @Path("user")
    public static class User {
        @GET
        @Path("{name}")
        public String get(@PathParam("name") String name){
            return "Hello " + name;
        }

        @GET
        @Priority(Priority.High)
        public String list(){
            return "should not match list";
        }

    }
}
