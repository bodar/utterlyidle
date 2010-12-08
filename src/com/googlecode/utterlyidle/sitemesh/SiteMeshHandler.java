package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Callables;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Predicates;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestHandler;
import com.googlecode.utterlyidle.Response;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.sitemesh.DecoratorRule.asTemplateName;
import static com.googlecode.utterlyidle.sitemesh.DecoratorRule.matchingRule;

public class SiteMeshHandler implements RequestHandler {
    private final RequestHandler requestHandler;
    private final BasePath base;
    private final Includer include;
    private final DecoratorRules decoratorRules;
    private final StringTemplateGroup group;

    public SiteMeshHandler(final RequestHandler requestHandler, final BasePath base, final Includer include, final DecoratorRules decoratorRules, final StringTemplateGroup templateGroup) {
        this.requestHandler = requestHandler;
        this.base = base;
        this.include = include;
        this.decoratorRules = decoratorRules;
        group = templateGroup;
    }

    public void handle(final Request request, final Response response) throws Exception {
        requestHandler.handle(request, response.output(new SiteMeshOutputStream(request, response, response.output())));
    }

    class SiteMeshOutputStream extends FilterOutputStream {
        private final Request request;
        private final Response response;
        private final OutputStream destination;

        public SiteMeshOutputStream(Request request, Response response, OutputStream destination) {
            super(new ByteArrayOutputStream()); // this is 'out'
            this.request = request;
            this.response = response;
            this.destination = destination;
        }

        @Override
        public void close() throws IOException {
            final Option<TemplateName> templateName = decoratorRules.getTemplateNameFor(request, response);
            final Option<StringTemplateDecorator> decorator = templateName.map(asDecorator());
            final OutputStreamWriter writer = new OutputStreamWriter(destination);
            writer.write(decorator.fold(getOriginalContent(), decorate()));
            writer.close();
            super.close();
        }

        private String getOriginalContent() {
            return out.toString();
        }

        private Callable2<String, StringTemplateDecorator, String> decorate() {
            return new Callable2<String, StringTemplateDecorator, String>() {
                public String call(String value, StringTemplateDecorator stringTemplateDecorator) throws Exception {
                    return stringTemplateDecorator.decorate(new PropertyMapParser().parse(value)).toString();
                }
            };
        }

        private Callable1<? super TemplateName, StringTemplateDecorator> asDecorator() {
            return new Callable1<TemplateName, StringTemplateDecorator>() {
                public StringTemplateDecorator call(TemplateName templateName) throws Exception {
                    StringTemplate template = group.getInstanceOf(templateName.name());
                    return new StringTemplateDecorator(template, include, base, request.query());
                }
            };
        }
    }


}