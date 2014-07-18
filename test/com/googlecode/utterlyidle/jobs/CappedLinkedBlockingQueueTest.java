package com.googlecode.utterlyidle.jobs;

import org.junit.Test;

public class CappedLinkedBlockingQueueTest {
    @Test
    public void shouldNotThrowExceptionWhenCapacityIsReached() throws Exception {
        CappedLinkedBlockingQueue<Integer> queue = new CappedLinkedBlockingQueue<Integer>(1);
        queue.add(1);
        queue.add(1);
    }
}
