package com.googlecode.utterlyidle.jobs.schedule;

import com.googlecode.lazyrecords.memory.MemoryRecords;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.totallylazy.time.StoppedClock;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.jobs.UtterlyIdleRecords;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.matchers.NumberMatcher.is;
import static com.googlecode.utterlyidle.jobs.schedule.Schedule.INTERVAL;
import static org.junit.Assert.assertThat;

public class HttpSchedulerTest {
    private String request = RequestBuilder.get("/test").build().toString();
    private Schedule schedule = Schedule.schedule(UUID.randomUUID()).interval(10L).request(request);
    private final StubScheduler stub = new StubScheduler();
    private final HttpScheduler httpScheduler = new HttpScheduler(new Schedules(new UtterlyIdleRecords(new MemoryRecords())), stub, null, new StoppedClock(Dates.date(2001, 1, 1)));

    @Test
    public void scheduleRequest() throws Exception {
        UUID id = httpScheduler.schedule(schedule);

        assertThat(httpScheduler.schedules().size(), is(1));
        assertThat(httpScheduler.schedule(id).get().get(INTERVAL), CoreMatchers.is(10L));
        assertThat(stub.delay, CoreMatchers.is(10L));
    }

    @Test
    public void rescheduleRequest() throws Exception {
        UUID id = httpScheduler.schedule(schedule);
        assertThat(stub.delay, CoreMatchers.is(10L));

        httpScheduler.schedule(schedule.interval(20L));

        assertThat(httpScheduler.schedules().size(), is(1));
        assertThat(httpScheduler.schedule(id).get().get(INTERVAL), CoreMatchers.is(20L));
        assertThat(stub.delay, CoreMatchers.is(20L));
    }

    @Test
    public void removeScheduledJob() throws Exception {
        UUID id = httpScheduler.schedule(schedule);
        httpScheduler.remove(id);
        assertThat(httpScheduler.schedules().size(), is(0));
    }

    private static class StubScheduler implements Scheduler {
        public long delay;

        @Override
        public void schedule(UUID id, Callable<?> command, Option<Date> start, long numberOfSeconds) {
            this.delay = numberOfSeconds;
        }

        public void cancel(UUID id) {
        }
    }
}