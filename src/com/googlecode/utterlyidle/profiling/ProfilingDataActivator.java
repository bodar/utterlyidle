package com.googlecode.utterlyidle.profiling;

import com.googlecode.utterlyidle.Redirector;
import com.googlecode.utterlyidle.Request;

import java.util.concurrent.Callable;

import static com.googlecode.utterlyidle.profiling.ProfilingHandler.shouldProfile;

public class ProfilingDataActivator implements Callable<ProfilingData> {
    private final Request request;
    private Redirector redirector;

    public ProfilingDataActivator(Request request, Redirector redirector) {
        this.request = request;
        this.redirector = redirector;
    }

    @Override
    public ProfilingData call() throws Exception {
        if (!shouldProfile(request)) {
            return IgnoreProfilingData.Instance;
        }
        return new InMemoryProfilingData(redirector);
    }

}
