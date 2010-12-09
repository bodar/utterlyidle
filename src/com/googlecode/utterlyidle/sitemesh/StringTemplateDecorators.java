package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.UrlStringTemplateGroup;
import com.googlecode.utterlyidle.io.Url;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import java.util.ArrayList;
import java.util.List;

public class StringTemplateDecorators implements Decorators {
    private final StringTemplateGroup group;
    private final List<DecoratorRule> rules = new ArrayList<DecoratorRule>();
    private final Includer include = new Includer();
    private final BasePath base;

    public StringTemplateDecorators(Url url, BasePath base) {
        this.base = base;
        group = new UrlStringTemplateGroup(url);
    }

    public Decorators add(DecoratorRule rule) {
        rules.add(rule);
        return this;
    }

    public Decorator getDecoratorFor(Request request, Response response) {
        for (DecoratorRule rule : rules) {
            if (rule.matches(Pair.pair(request, response))) {
                return getDecoratorFor(request, rule.templateName());
            }
        }
        return new NoneDecorator();
    }

    private Decorator getDecoratorFor(Request request, TemplateName templateName) {
        if (templateName.equals(TemplateName.NONE)) {
            return new NoneDecorator();
        }
        StringTemplate template = group.getInstanceOf(templateName.name());
        return new StringTemplateDecorator(template, include, base, request.query());
    }
}
