package com.googlecode.utterlyidle.dsl;

import com.googlecode.totallylazy.Function1;
import com.googlecode.utterlyidle.Binding;
import com.googlecode.utterlyidle.MediaType;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.definedParam;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.get;
import static com.googlecode.utterlyidle.dsl.BindingBuilder.pathParam;
import static java.lang.String.format;
import static java.util.regex.Pattern.quote;

public class StaticBindingBuilder implements Callable<Binding[]> {
    private final URL base;
    private String path;
    private Map<String, String> extensionToMimeType = new HashMap<String, String>() {{
        put("png", MediaType.IMAGE_PNG);
        put("gif", MediaType.IMAGE_GIF);
        put("jpg", MediaType.IMAGE_JPEG);
        put("jpeg", MediaType.IMAGE_JPEG);
        put("ico", MediaType.IMAGE_X_ICON);
        put("svg", MediaType.IMAGE_SVG);
        put("js", MediaType.TEXT_JAVASCRIPT);
        put("map", MediaType.APPLICATION_JAVASCRIPT);
        put("css", MediaType.TEXT_CSS);
        put("less", MediaType.TEXT_CSS);
        put("html", MediaType.TEXT_HTML);
        put("xml", MediaType.TEXT_XML);
        put("xsl", MediaType.TEXT_XML);
        put("csv", MediaType.TEXT_CSV);
        put("txt", MediaType.TEXT_PLAIN);
        put("appcache", MediaType.TEXT_CACHE_MANIFEST);
        put("otf", MediaType.FONT_SFNT);
        put("ttf", MediaType.FONT_SFNT);
        put("woff", MediaType.FONT_WOFF);
    }};

    public StaticBindingBuilder(URL base) {
        this.base = base;
    }

    public Binding[] call() throws Exception {
        return build();
    }

    private Binding[] build() {
        return sequence(extensionToMimeType.entrySet()).map(asBinding()).toArray(Binding.class);
    }

    public StaticBindingBuilder set(String extension, String mimeType) {
        extensionToMimeType.put(extension, mimeType);
        return this;
    }

    private Function1<Map.Entry<String, String>, Binding> asBinding() {
        return new Function1<Map.Entry<String, String>, Binding>() {
            public Binding call(Map.Entry<String, String> entry) throws Exception {
                return get(format("%s/{filename:.+\\.%s}", path, quote(entry.getKey()))).
                        produces(entry.getValue()).
                        resource(method(on(StaticResources.class).
                                get(definedParam(base), pathParam(String.class, "filename")))).
                        hidden(true).
                        build();
            }
        };
    }

    public static StaticBindingBuilder in(URL base) {
        return new StaticBindingBuilder(base);
    }

    public StaticBindingBuilder path(String path) {
        this.path = path;
        return this;
    }
}