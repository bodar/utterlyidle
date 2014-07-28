package com.googlecode.utterlyidle;

import com.googlecode.yadic.Resolver;
import com.googlecode.yadic.closeable.CloseableContainer;

public class DefaultContainerFactory implements ContainerFactory {
    @Override
    public CloseableContainer newCloseableContainer() {
        return CloseableContainer.closeableContainer();
    }

    @Override
    public CloseableContainer newCloseableContainer(final Resolver<?> parent) {
        return CloseableContainer.closeableContainer(parent);
    }
}
