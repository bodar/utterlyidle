package com.googlecode.utterlyidle.servlet;

import com.googlecode.utterlyidle.Application;
import com.googlecode.yadic.SimpleContainer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import java.io.IOException;

import static com.googlecode.yadic.resolvers.Resolvers.resolve;

public class ApplicationStarter implements ServletContextListener {
    public static final String KEY = Application.class.getCanonicalName();

    private Application createApplication(ServletContext servletContext) {
        String className = servletContext.getInitParameter(getClass().getName());
        if (className == null || className.equals("")) {
            throw new UnsupportedOperationException("The web.xml must container a context-param called " + getClass().getName());
        }
        try {
            Class<?> aClass = Class.forName(className);
            Object instance = resolve(new SimpleContainer().add(aClass), aClass);
            if (!(instance instanceof Application)) {
                throw new UnsupportedOperationException(className + " must implement " + Application.class.getName());
            }
            return (Application) instance;
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public void contextInitialized(ServletContextEvent event) {
        final ServletContext context = event.getServletContext();
        setApplication(context, createApplication(context));
    }

    public static void setApplication(ServletContext context, Application application) {
        application.add(new ServletModule(context));
        context.setAttribute(KEY, application);
    }

    public static Application getApplication(final ServletContext servletContext) {
        return (Application) servletContext.getAttribute(KEY);
    }

    public void contextDestroyed(ServletContextEvent event) {
        try {
            getApplication(event.getServletContext()).close();
        } catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}