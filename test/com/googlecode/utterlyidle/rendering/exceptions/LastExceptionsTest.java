package com.googlecode.utterlyidle.rendering.exceptions;

import com.googlecode.totallylazy.Block;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.numbers.Numbers;
import com.googlecode.utterlyidle.RequestBuilder;
import org.junit.Test;

import java.util.Date;

import static com.googlecode.totallylazy.Sequences.sequence;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LastExceptionsTest {

    private static final int SIZE = 20;
    private LastExceptions lastExceptions = new LastExceptions(SIZE);

    @Test
    public void storesNItems() throws Exception {
        Sequence<Number> numbers = Numbers.range(1, SIZE).memorise();

        addNumbersToLastExceptions(numbers);

        verifyLastExceptionsContains(numbers);
    }

    @Test
    public void storesLastNItems() throws Exception {
        Sequence<Number> numbers = Numbers.range(1, SIZE * 2).memorise();

        addNumbersToLastExceptions(numbers);

        verifyLastExceptionsContains(numbers.drop(SIZE));
    }

    private void verifyLastExceptionsContains(Sequence<Number> expected) {
        Sequence<StoredException> exceptions = sequence(lastExceptions).memorise();
        assertThat(exceptions.size(), is(expected.size()));

        expected.zip(exceptions.map(StoredException.exception())).forEach(new Block<Pair<Number, String>>() {
            @Override
            protected void execute(Pair<Number, String> pair) throws Exception {
                assertThat(pair.first().toString(), is(pair.second()));
            }
        });
    }

    private void addNumbersToLastExceptions(Sequence<Number> numbers) {
        numbers.forEach(new Block<Number>() {
            @Override
            protected void execute(Number number) throws Exception {
                lastExceptions.put(new Date(), RequestBuilder.get("/foo").build(), number.toString());
            }
        });
    }
}
