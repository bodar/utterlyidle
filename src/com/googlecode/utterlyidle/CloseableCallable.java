package com.googlecode.utterlyidle;

import java.io.Closeable;
import java.util.concurrent.Callable;

public interface CloseableCallable<T> extends Callable<T>, Closeable {
}
