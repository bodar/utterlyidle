package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

public interface Decorators {
    Decorator getDecoratorFor(Request request, Response response);
}
