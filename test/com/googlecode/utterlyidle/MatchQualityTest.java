package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.annotations.DefaultValue;
import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.annotations.PathParam;
import com.googlecode.utterlyidle.annotations.Priority;
import com.googlecode.utterlyidle.annotations.QueryParam;
import org.junit.Test;

import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.utterlyidle.ApplicationBuilder.application;
import static com.googlecode.utterlyidle.Request.Builder.get;
import static org.hamcrest.MatcherAssert.assertThat;

public class MatchQualityTest {
    @Test
    public void matchesLongestPathsBeforePriority() throws Exception {
        assertThat(application().addAnnotated(User.class).responseAsString(get("user/dan")), is("Hello dan"));
    }

    @Test
    public void matchesNamedParametersBeforeDefaultOnes() throws Exception {
        assertThat(application().addAnnotated(Loser.class).responseAsString(get("loser?firstName=Foghorn&lastName=Leghorn")), is("Hello Foghorn Leghorn"));
        assertThat(application().addAnnotated(Loser.class).responseAsString(get("loser?firstName=Stuart")), is("Hello Stuart with a shoe size of 10.5"));
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

    @Path("loser")
    public static class Loser {
        @GET
        public String shoeGreeter(@QueryParam("firstName") String firstName, @QueryParam("shoeSize") @DefaultValue("10.5") String shoeSize) {
            return String.format("Hello %s with a shoe size of %s", firstName, shoeSize);
        }

        @GET
        public String nameGreeter(@QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName) {
            return String.format("Hello %s %s", firstName, lastName);
        }
    }
}
