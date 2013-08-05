package com.googlecode.utterlyidle.schedules;

import com.googlecode.lazyrecords.memory.MemoryRecords;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.totallylazy.time.StoppedClock;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.utterlyidle.jobs.UtterlyIdleRecords;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.matchers.NumberMatcher.is;
import static org.junit.Assert.assertThat;

public class HttpSchedulerTest {
    private String request = RequestBuilder.get("/test").build().toString();
    private Schedule schedule = Schedule.schedule(UUID.randomUUID()).interval(10L).request(request);
    private final StubScheduler stub = new StubScheduler();
    private final HttpScheduler httpScheduler = new HttpScheduler(new SchedulerState(), new Schedules(new UtterlyIdleRecords(new MemoryRecords())), stub, null, new StoppedClock(Dates.date(2001, 1, 1)));

    @Test
    public void scheduledRequestIsNotRunUntilSchedulerIsStarted() throws Exception {
        httpScheduler.schedule(schedule);
        assertThat(stub.scheduled, Matchers.is(0));

        httpScheduler.start();
        assertThat(stub.scheduled, Matchers.is(1));
    }

    @Test
    public void startingMultipleTimesDoesNotRunScheduledTasksMultipleTimes() throws Exception {
        httpScheduler.schedule(schedule);
        assertThat(stub.scheduled, Matchers.is(0));

        httpScheduler.start();
        httpScheduler.start();
        assertThat(stub.scheduled, Matchers.is(1));
    }

    @Test
    public void scheduleRequest() throws Exception {
        UUID id = httpScheduler.schedule(schedule);

        assertThat(httpScheduler.schedules().size(), is(1));
        assertThat(httpScheduler.schedule(id).get().get(SchedulesDefinition.interval), CoreMatchers.is(10L));

        httpScheduler.start();

        assertThat(stub.delay, CoreMatchers.is(10L));
    }

    @Test
    public void rescheduleRequest() throws Exception {
        UUID id = httpScheduler.schedule(schedule);
        httpScheduler.start();
        assertThat(stub.delay, CoreMatchers.is(10L));

        httpScheduler.schedule(schedule.interval(20L));

        assertThat(httpScheduler.schedules().size(), is(1));
        assertThat(httpScheduler.schedule(id).get().get(SchedulesDefinition.interval), CoreMatchers.is(20L));
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
        public int scheduled = 0;

        @Override
        public void schedule(UUID id, Callable<?> command, Option<Date> start, long numberOfSeconds) {
            this.delay = numberOfSeconds;
            scheduled++;
        }

        public void cancel(UUID id) {
        }
    }
}