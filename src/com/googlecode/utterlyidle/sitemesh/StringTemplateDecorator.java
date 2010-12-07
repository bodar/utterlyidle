package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.LazyException;
import com.googlecode.totallylazy.Maps;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.QueryParameters;
import org.antlr.stringtemplate.NoIndentWriter;
import org.antlr.stringtemplate.StringTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import static com.googlecode.totallylazy.Sequences.sequence;

public class StringTemplateDecorator {
    private final StringTemplate template;

    public StringTemplateDecorator(StringTemplate template) {
        this.template = template;
    }

    public StringTemplateDecorator setInclude(PageMap include) {
        template.setAttribute("include", include);
        return this;
    }

    public StringTemplateDecorator setBase(BasePath base) {
        template.setAttribute("base", base);
        return this;
    }

    public StringTemplateDecorator setQueryString(QueryParameters queryParameters) {
        template.setAttribute("query", sequence(queryParameters).fold(Maps.<String, List<String>>map(), Maps.<String, String>asMultiValuedMap()));
        return this;
    }

    public StringTemplateDecorator setContent(PropertyMap properties) {
        template.setAttribute("properties", properties);
        template.setAttribute("head", properties.get("head"));
        template.setAttribute("title", properties.get("title"));
        template.setAttribute("body", properties.get("body"));
        return this;
    }

    public void writeTo(Writer writer) {
        try {
            template.write(new NoIndentWriter(writer));
        } catch (IOException e) {
            throw new LazyException(e);
        }
    }

    @Override
    public String toString() {
        Writer writer = new StringWriter();
        writeTo(writer);
        return writer.toString();
    }
}
