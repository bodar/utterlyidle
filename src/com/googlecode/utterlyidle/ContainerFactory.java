package com.googlecode.utterlyidle;

import com.googlecode.yadic.Resolver;
import com.googlecode.yadic.closeable.CloseableContainer;

public interface ContainerFactory {
    CloseableContainer newCloseableContainer();

    CloseableContainer newCloseableContainer(Resolver<?> parent);
}
