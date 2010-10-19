package com.googlecode.utterlyidle.servlet;

import com.googlecode.utterlyidle.io.HierarchicalPath;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Callable;

public class ContextPath extends HierarchicalPath {
    protected ContextPath(String value) {
        super(value);
    }

    public static ContextPath contextPath(HttpServletRequest request) {
        return new ContextPath(request.getContextPath());
    }

    public static Callable<ContextPath> contextPath(final ServletContext context) {
        return new Callable<ContextPath>() {
            public ContextPath call() throws Exception {
                return new ContextPath(context.getContextPath());
            }
        };
    }
}
