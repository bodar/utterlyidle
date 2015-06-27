package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.template.Renderer;
import com.googlecode.totallylazy.template.Templates;
import com.googlecode.utterlyidle.Request;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.Resolver;
import com.googlecode.yadic.SimpleContainer;
import com.googlecode.yadic.generics.TypeFor;

import java.net.URL;
import java.util.Map;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.io.Uri.uri;
import static com.googlecode.utterlyidle.sitemesh.Decorators.add;

public class StringTemplateDecorators implements DecoratorProvider {
    private final Templates group;
    private final Resolver resolver;

    public StringTemplateDecorators(final Templates group, final Resolver resolver) {
        this.group = group;
        this.resolver = resolver;
    }

    public StringTemplateDecorators(final URL templatesUrl, final Resolver resolver) {
        this(Templates.defaultTemplates(uri(templatesUrl)), resolver);
    }

    public Decorator get(TemplateName templateName, Request request) {
        if (templateName.equals(TemplateName.NONE)) {
            return new NoneDecorator();
        }
        SimpleContainer container = new SimpleContainer(resolver);
        container.addType(new TypeFor<Renderer<Map<String, Object>>>() { }.get(), type -> group.get(templateName.value()));
        return container.
                addInstance(Request.class, request).
                add(StringTemplateDecorator.class).
                get(StringTemplateDecorator.class);
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
