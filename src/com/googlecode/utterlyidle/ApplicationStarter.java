package com.googlecode.utterlyidle;

import com.googlecode.utterlyidle.servlet.AttributeMap;
import com.googlecode.utterlyidle.servlet.ContextPath;
import com.googlecode.utterlyidle.servlet.WarRoot;
import com.googlecode.yadic.SimpleContainer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static com.googlecode.utterlyidle.servlet.AttributeMap.attributeMap;
import static com.googlecode.utterlyidle.servlet.ContextPath.contextPath;
import static com.googlecode.utterlyidle.servlet.WarRoot.warRoot;

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
        ServletContext context = event.getServletContext();
        Application application = createApplication(context);
        application.applicationScope().
                addActivator(WarRoot.class, warRoot(context)).
                addActivator(ContextPath.class, contextPath(context)).
                addActivator(AttributeMap.class, attributeMap(context));
        context.setAttribute(Application.class.getCanonicalName(), application);
    }

    public void contextDestroyed(ServletContextEvent event) {
    }
}