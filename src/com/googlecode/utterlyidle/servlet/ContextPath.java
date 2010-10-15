package com.googlecode.utterlyidle.servlet;

import com.googlecode.utterlyidle.io.HierarchicalPath;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class ContextPath extends HierarchicalPath {
    protected ContextPath(String value) {
        super(value);
    }

    public static ContextPath contextPath(HttpServletRequest request) {
        return new ContextPath(request.getContextPath());
    }

    public static ContextPath contextPath(ServletContext context) {
        return new ContextPath(context.getContextPath());
    }
}
