package com.googlecode.utterlyidle.servlet;

import com.googlecode.utterlyidle.Application;
import com.googlecode.yadic.SimpleContainer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ApplicationStarter implements ServletContextListener {
    private Application createApplication(ServletContext servletContext) {
        String className = servletContext.getInitParameter(getClass().getName());
        if (className == null || className.equals("")) {
            throw new UnsupportedOperationException("The web.xml must container a context-param called " + getClass().getName());
        }
        try {
            Class<?> aClass = Class.forName(className);
            Object instance = new SimpleContainer().add(aClass).resolve(aClass);
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
        Application application = createApplication(context);
        application.add(new ServletModule(context));
        context.setAttribute(Application.class.getCanonicalName(), application);
    }

    public void contextDestroyed(ServletContextEvent event) {
    }
}