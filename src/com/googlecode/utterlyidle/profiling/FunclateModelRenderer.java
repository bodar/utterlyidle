package com.googlecode.utterlyidle.profiling;

import com.googlecode.funclate.Model;
import com.googlecode.funclate.stringtemplate.EnhancedStringTemplateGroup;
import com.googlecode.utterlyidle.Renderer;
import org.antlr.stringtemplate.StringTemplate;

import static com.googlecode.totallylazy.URLs.packageUrl;

public class FunclateModelRenderer implements Renderer<Model> {
    private final Class resource;
    private final String fileName;

    public FunclateModelRenderer(Class resource) {
        this(resource, resource.getSimpleName());
    }

    public FunclateModelRenderer(Class resource, String fileName) {
        this.resource = resource;
        this.fileName = fileName;
    }

    public static FunclateModelRenderer funclateModelRenderer(Class resource, String fileName) {
        return new FunclateModelRenderer(resource, fileName);
    }

    public static FunclateModelRenderer funclateModelRenderer(Class resource) {
        return funclateModelRenderer(resource, resource.getSimpleName());
    }

    @Override
    public String render(Model model) throws Exception {
        EnhancedStringTemplateGroup group = new EnhancedStringTemplateGroup(packageUrl(resource));
        StringTemplate template = group.getInstanceOf(fileName, model.toMap());
        return template.toString();

    }
}
