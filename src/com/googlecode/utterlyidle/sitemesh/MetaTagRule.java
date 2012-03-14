package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

public class MetaTagRule implements DecoratorRule {
    private final String name;
    private String templateName;

    public MetaTagRule(String name) {
        this.name = name;
    }

    public static DecoratorRule metaTagRule(String name) {
        return new MetaTagRule(name);
    }

    public TemplateName templateName() {
        if(templateName == null){
            throw new IllegalStateException("You must call matches first");
        }
        return TemplateName.templateName(templateName);
    }

    public boolean matches(Pair<Request, Response> pair) {
        try {
            Response response = pair.second();

            PropertyMap propertyMap = new PropertyMapParser().parse(response.entity().asString());
            templateName = propertyMap.getPropertyMap("meta").getPropertyMap(this.name).toString();
            if (templateName != null && templateName.length() != 0) {
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }
}
