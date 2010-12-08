package com.googlecode.utterlyidle.sitemesh;

import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestHandler;
import com.googlecode.utterlyidle.Response;
import org.antlr.stringtemplate.StringTemplate;

public class SiteMeshHandler implements RequestHandler {
    private final RequestHandler requestHandler;
    private final BasePath base;
    private final Includer include;

    public SiteMeshHandler(final RequestHandler requestHandler, final BasePath base, final Includer include) {
        this.requestHandler = requestHandler;
        this.base = base;
        this.include = include;
    }

    public void handle(final Request request, final Response response) throws Exception {
        StringTemplate template = new StringTemplate("$body$ World!");
        StringTemplateDecorator decorator = new StringTemplateDecorator(template, include, base,request.query());
        Response decoratetResponse = response.output(new SiteMeshOutputStream(response.output(), decorator));
        requestHandler.handle(request, decoratetResponse);
    }

}