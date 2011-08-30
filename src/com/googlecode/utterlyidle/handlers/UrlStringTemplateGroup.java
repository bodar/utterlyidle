package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.RequestBuilder;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import java.io.BufferedReader;
import java.io.StringReader;
import java.net.URL;

public class UrlStringTemplateGroup extends StringTemplateGroup {
    public UrlStringTemplateGroup(URL baseUrl) {
        super(baseUrl.toString(), baseUrl.toString());
    }

    @Override
    protected StringTemplate loadTemplate(String name, String fileName) {
        try {
            String response = new String(new ClientHttpHandler().handle(RequestBuilder.get(fileName).build()).bytes());
            return loadTemplate(name, new BufferedReader(new StringReader(response)));
        } catch (Exception e) {
            return null;
        }
    }
}
