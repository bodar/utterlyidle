package com.googlecode.utterlyidle;

public interface HttpMessage<T extends HttpMessage<T>> {
    HeaderParameters headers();

    default T headers(HeaderParameters value) {
        return create(value, entity());
    }

    Entity entity();

    default T entity(Entity value) {
        return create(headers(), value);
    }

    T create(HeaderParameters headers, Entity entity);

    interface Builder {

    }
}
