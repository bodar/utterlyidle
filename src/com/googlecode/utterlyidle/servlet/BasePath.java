package com.googlecode.utterlyidle.servlet;

import com.googlecode.utterlyidle.io.HierarchicalPath;

import javax.servlet.http.HttpServletRequest;

public class BasePath extends HierarchicalPath {
    public static BasePath basePath(HttpServletRequest request){
        return new BasePath(request.getContextPath() + request.getServletPath());
    }

    public static BasePath basePath(String path){
        return new BasePath(path);
    }

    protected BasePath(String v){
        super(v);
    }
}