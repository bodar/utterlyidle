package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.Callers;
import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.yadic.Container;

import javax.naming.spi.Resolver;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Callables.returns;
import static com.googlecode.totallylazy.Callers.call;

public class Auditors implements Auditor{
    private final List<Callable<Auditor>> auditors = new ArrayList<Callable<Auditor>>();
    private final Container container;

    public Auditors(Container container) {
        this.container = container;
    }

    public void audit(Pair<Request, Date> request, Pair<Response, Date> response) {
        for (Callable<Auditor> callable : auditors) {
            Auditor auditor = call(callable);
            try {
                auditor.audit(request, response);
            } catch (Exception e) {
                // continue auditing with other auditors
            }
        }
    }

    public Auditors add(Auditor auditor) {
        auditors.add(returns(auditor));
        return this;
    }

    public Auditors add(final Class<? extends Auditor> auditor) {
        auditors.add(resolve(auditor));
        return this;
    }

    private Callable<Auditor> resolve(final Class<? extends Auditor> auditor) {
        return new Callable<Auditor>() {
            public Auditor call() throws Exception {
                if(!container.contains(auditor)){
                    container.add(auditor);
                }
                return container.get(auditor);
            }
        };
    }
}
