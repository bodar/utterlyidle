package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.LazyException;
import com.googlecode.totallylazy.Maps;
import com.googlecode.utterlyidle.handlers.InternalHttpHandler;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.QueryParameters;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Requests;
import com.googlecode.utterlyidle.handlers.HttpClient;
import org.antlr.stringtemplate.NoIndentWriter;
import org.antlr.stringtemplate.StringTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class StringTemplateDecorator implements Decorator {
    private final StringTemplate template;
    private final HttpHandler httpHandlerForIncludes;
    private final BasePath base;
    private final QueryParameters queryParameters;

    public StringTemplateDecorator(StringTemplate template, HttpClient httpHandlerForIncludes, BasePath base, QueryParameters queryParameters) {
        this.template = template;
        this.httpHandlerForIncludes = httpHandlerForIncludes;
        this.base = base;
        this.queryParameters = queryParameters;
    }

    public StringTemplateDecorator(StringTemplate template, HttpClient httpHandlerForIncludes, BasePath base, Request request) {
        this(template, httpHandlerForIncludes, base, Requests.query(request));
    }

    public Decorator setContent(PropertyMap content) throws IOException {
        template.setAttribute("include", new PageMap(httpHandlerForIncludes));
        template.setAttribute("base", base);
        template.setAttribute("query", Maps.multiMap(queryParameters));
        template.setAttribute("properties", content);
        template.setAttribute("meta", content.getPropertyMap("meta"));
        template.setAttribute("head", content.get("head"));
        template.setAttribute("title", content.get("title"));
        template.setAttribute("body", content.get("body"));
        return this;
    }

    public String decorate(String content) throws IOException {
        return setContent(new PropertyMapParser().parse(content)).toString();
    }

    public void writeTo(Writer writer) {
        try {
            template.write(new NoIndentWriter(writer));
        } catch (IOException e) {
            throw LazyException.lazyException(e);
        }
    }

    public String toString() {
        Writer writer = new StringWriter();
        writeTo(writer);
        return writer.toString();
    }
}
