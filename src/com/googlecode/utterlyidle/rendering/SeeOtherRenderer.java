package com.googlecode.utterlyidle.rendering;

import com.googlecode.totallylazy.Maps;
import com.googlecode.totallylazy.template.Templates;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.Renderer;

public class SeeOtherRenderer implements Renderer<String> {

    private BasePath basePath;

    public SeeOtherRenderer(BasePath basePath) {
        this.basePath = basePath;
    }

    @Override
    public String render(String value) throws Exception {
        return Templates.templates(getClass()).addDefault().extension("html").get("seeOther").
                render(Maps.map("base", basePath, "location", value));
    }
}
