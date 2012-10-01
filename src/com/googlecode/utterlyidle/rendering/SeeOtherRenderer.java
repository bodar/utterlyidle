package com.googlecode.utterlyidle.rendering;

import com.googlecode.funclate.stringtemplate.EnhancedStringTemplateGroup;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.Renderer;
import org.antlr.stringtemplate.StringTemplate;

import static com.googlecode.totallylazy.URLs.packageUrl;

public class SeeOtherRenderer implements Renderer<String> {

    private BasePath basePath;

    public SeeOtherRenderer(BasePath basePath) {
        this.basePath = basePath;
    }

    @Override
    public String render(String value) throws Exception {
        StringTemplate template = new EnhancedStringTemplateGroup(packageUrl(getClass())).getInstanceOf("seeOther");
        template.setAttribute("base", basePath);
        template.setAttribute("location", value);
        return template.toString();
    }
}
