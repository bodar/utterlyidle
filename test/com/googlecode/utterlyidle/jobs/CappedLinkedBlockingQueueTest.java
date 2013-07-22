package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Sequence;
import org.junit.Test;

import java.util.Queue;

import static com.googlecode.totallylazy.Sequences.repeat;
import static com.googlecode.totallylazy.matchers.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CappedLinkedBlockingQueueTest {
    @Test
    public void shouldNotThrowExceptionWhenCapacityIsReachedConcurrently() throws Exception {
        CappedLinkedBlockingQueue<Integer> queue = new CappedLinkedBlockingQueue<Integer>(1);
        int count = 1000;
        Sequence<Boolean> take = repeat(queue).take(count).mapConcurrently(add(count));
        assertThat(take, is(repeat(true).take(count)));

    }

    private Callable1<Queue<Integer>, Boolean> add(final int count) {
        return new Callable1<Queue<Integer>, Boolean>() {
            @Override
            public Boolean call(Queue<Integer> queue) throws Exception {
                for (int i = 0; i < count; i++) {
                    queue.add(1);
                }
                return true;
            }
        };
    }
}
