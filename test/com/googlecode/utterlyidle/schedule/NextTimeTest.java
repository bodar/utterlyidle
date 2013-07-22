package com.googlecode.utterlyidle.schedule;

import com.googlecode.totallylazy.time.FixedClock;
import org.junit.Test;

import static com.googlecode.totallylazy.time.Dates.date;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class NextTimeTest {
    private static final FixedClock MIDDAY = new FixedClock(date(2012, 1, 1, 12));

    @Test
    public void shouldCalculateTimeToday() throws Exception {
        assertThat(NextTime.nextTime("1300", MIDDAY).value(), is(date(2012, 1, 1, 13)));
        assertThat(NextTime.nextTime("1200", MIDDAY).value(), is(date(2012, 1, 1, 12)));
    }

    @Test
    public void shouldCalculateTimeTomorrow() throws Exception {
        assertThat(NextTime.nextTime("1100", MIDDAY).value(), is(date(2012, 1, 2, 11)));
    }
}
