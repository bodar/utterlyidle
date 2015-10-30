package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.totallylazy.Maps;
import com.googlecode.totallylazy.template.Renderer;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.QueryParameters;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Requests;
import com.googlecode.utterlyidle.handlers.HttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StringTemplateDecorator implements Decorator {
    private final Renderer<Map<String, Object>> template;
    private final HttpHandler httpHandlerForIncludes;
    private final BasePath base;
    private final QueryParameters queryParameters;

    public StringTemplateDecorator(Renderer<Map<String, Object>> template, HttpClient httpHandlerForIncludes, BasePath base, QueryParameters queryParameters) {
        this.template = template;
        this.httpHandlerForIncludes = httpHandlerForIncludes;
        this.base = base;
        this.queryParameters = queryParameters;
    }

    public StringTemplateDecorator(Renderer<Map<String, Object>> template, HttpClient httpHandlerForIncludes, BasePath base, Request request) {
        this(template, httpHandlerForIncludes, base, Requests.query(request));
    }

    public String decorate(String content) throws IOException {
        PropertyMap propertyMap = new PropertyMapParser().parse(content);
        Map<String, Object> map = new HashMap<>();
        map.put("include", new PageMap(httpHandlerForIncludes));
        map.put("base", base);
        map.put("query", Maps.multiMap(queryParameters));
        map.put("properties", propertyMap);
        map.put("meta", propertyMap.getPropertyMap("meta"));
        map.put("head", propertyMap.get("head"));
        map.put("title", propertyMap.get("title"));
        map.put("body", propertyMap.get("body"));
        map.put("div", propertyMap.get("div"));
        try {
            return template.render(map);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

}
