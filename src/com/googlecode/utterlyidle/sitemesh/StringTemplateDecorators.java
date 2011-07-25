package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.handlers.UrlStringTemplateGroup;
import com.googlecode.utterlyidle.io.Url;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.Resolver;
import com.googlecode.yadic.SimpleContainer;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import java.net.URL;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.sitemesh.Decorators.add;

public class StringTemplateDecorators implements DecoratorProvider {
    private final StringTemplateGroup group;
    private final Resolver resolver;

    public StringTemplateDecorators(final StringTemplateGroup group, final Resolver resolver) {
        this.group = group;
        this.resolver = resolver;
    }

    public StringTemplateDecorators(final URL templatesUrl, final Resolver resolver) {
        this(new UrlStringTemplateGroup(Url.url(templatesUrl)), resolver);
    }

    public Decorator get(TemplateName templateName) {
        if (templateName.equals(TemplateName.NONE)) {
            return new NoneDecorator();
        }
        return new SimpleContainer(resolver).
                addInstance(StringTemplate.class, group.getInstanceOf(templateName.value())).
                add(StringTemplateDecorator.class).get(StringTemplateDecorator.class);
    }

    public static ActivateSiteMeshModule stringTemplateDecorators(final URL templatesUrl, final DecoratorRule... decoratorRules) {
        return stringTemplateDecorators(templatesUrl, sequence(decoratorRules));
    }

    public static ActivateSiteMeshModule stringTemplateDecorators(final URL templatesUrl, final Sequence<DecoratorRule> sequence) {
        return new ActivateSiteMeshModule() {
            @Override
            public Decorators addDecorators(Decorators decorators) {
                return sequence.fold(decorators, add());
            }

            @Override
            protected DecoratorProvider provider(Container container) {
                return new StringTemplateDecorators(templatesUrl, container);
            }
        };
    }


}
