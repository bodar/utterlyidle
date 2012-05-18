package com.googlecode.utterlyidle.examples;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.utterlyidle.RestApplication;
import com.googlecode.utterlyidle.StreamingWriter;
import com.googlecode.utterlyidle.handlers.ResponseHandlers;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.PerformanceModule;
import com.googlecode.utterlyidle.modules.ResponseHandlersModule;
import com.googlecode.utterlyidle.profiling.ProfilingModule;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import static com.googlecode.totallylazy.Predicates.instanceOf;
import static com.googlecode.totallylazy.predicates.WherePredicate.where;
import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.ResponseBuilder.modify;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.get;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.queryParam;
import static com.googlecode.utterlyidle.dsl.DslBindings.binding;
import static com.googlecode.utterlyidle.handlers.HandlerRule.entity;
import static com.googlecode.utterlyidle.modules.Modules.bindingsModule;
import static com.googlecode.utterlyidle.modules.Modules.requestInstance;

public class HelloWorldApplication extends RestApplication {
    public HelloWorldApplication(BasePath basePath) {
        super(basePath,
                bindingsModule(annotatedClass(HelloWorld.class)),
                new ResponseHandlersModule() {
                    @Override
                    public Module addResponseHandlers(ResponseHandlers handlers) throws Exception {
                        handlers.add(where(entity(), instanceOf(Sequence.class)), streamingRenderer(new SequenceRenderer()));
                        return this;
                    }
                },
                new ProfilingModule(),
                bindingsModule(binding(get("/dsl").
                        resource(method(on(Properties.class).getProperty(queryParam(String.class, "name"), queryParam(String.class, "default")))))),
                requestInstance(System.getProperties()),
                new PerformanceModule());
    }

    private static <T> ResponseHandler streamingRenderer(final WritingRenderer<T> writingRenderer) {
        return new ResponseHandler() {
            @Override
            public Response handle(final Response response) throws Exception {
                return modify(response).entity(new StreamingWriter() {
                    @Override
                    public void write(Writer writer) throws IOException {
                        writingRenderer.renderTo((T) response.entity().value(), writer);
                    }
                }).build();
            }
        };
    }
}
