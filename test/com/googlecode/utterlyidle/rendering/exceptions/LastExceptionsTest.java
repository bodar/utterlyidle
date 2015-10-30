package com.googlecode.utterlyidle.rendering.exceptions;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.time.StoppedClock;
import org.junit.Test;

import static com.googlecode.totallylazy.Sequences.repeat;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.time.Dates.date;
import static com.googlecode.utterlyidle.Request.Builder.get;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LastExceptionsTest {
    private static final int SIZE = 20;
    private LastExceptions lastExceptions = new LastExceptions(new LastExceptionsSize(SIZE), new StoppedClock(date(2001, 1, 1)));

    @Test
    public void storesOnlyLastExceptions() throws Exception {
        Sequence<RuntimeException> exceptions = repeat(new RuntimeException()).take(SIZE * 2);

        addNumbersToLastExceptions(exceptions);

        verifyLastExceptionsContains(exceptions.drop(SIZE));
    }

    private void verifyLastExceptionsContains(Sequence<? extends Exception> expected) {
        Sequence<StoredException> exceptions = sequence(lastExceptions);
        assertThat(exceptions.size(), is(expected.size()));

        for (Pair<? extends Exception, Exception> pair : expected.zip(exceptions.map(StoredException.exception()))) {
            assertThat(pair.first(), is(pair.second()));
        }
    }

    private void addNumbersToLastExceptions(Sequence<? extends Exception> numbers) {
        numbers.each(exception -> {
            lastExceptions.put(get("/foo"), exception);
        });
    }
}
