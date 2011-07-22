package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callers;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.utterlyidle.io.Url;
import com.googlecode.utterlyidle.modules.BindingsModule;
import com.googlecode.utterlyidle.modules.Module;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.googlecode.utterlyidle.ServerConfiguration.defaultConfiguration;
import static com.googlecode.utterlyidle.dsl.DslBindings.bindings;
import static com.googlecode.utterlyidle.dsl.StaticBindingBuilder.in;

public class ApplicationBuilder {
    private final List<Module> modules = new ArrayList<Module>();

    public static ApplicationBuilder application() {
        return new ApplicationBuilder();
    }

    public ApplicationBuilder content(final URL baseUrl, final String path) {
        modules.add(new BindingsModule(bindings(in(baseUrl).path(path))));
        return this;
    }

    public Application build() {
        return new RestApplication(Sequences.sequence(modules).toArray(Module.class));
    }

    public Url start(ServerConfiguration configuration) {
        return Callers.call(new ServerActivator(build(), configuration)).getUrl();
    }

    public Url start() {
        return start(defaultConfiguration());
    }
}
