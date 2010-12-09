package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Option;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.UrlStringTemplateGroup;
import com.googlecode.utterlyidle.io.Url;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

public class StringTemplateDecorators implements Decorators {
    private final StringTemplateGroup group;
    private final DecoratorRules rules;
    private final Includer include = new Includer();
    private final BasePath base;

    public StringTemplateDecorators(Url url, DecoratorRules rules, BasePath base) {
        this.rules = rules;
        this.base = base;
        group = new UrlStringTemplateGroup(url);
    }

    public Decorator getDecoratorFor(Request request, Response response) {
        Option<TemplateName> templateName = rules.getTemplateNameFor(request, response);
        Option<Decorator> map = templateName.map(asDecorator(request));
        return  map.getOrElse(new NoneDecorator());
    }

    private Callable1<? super TemplateName, Decorator> asDecorator(final Request request) {
        return new Callable1<TemplateName, Decorator>() {
            public Decorator call(TemplateName templateName) throws Exception {
                StringTemplate template = group.getInstanceOf(templateName.name());
                return new StringTemplateDecorator(template, include, base, request.query());
            }
        };
    }

}
