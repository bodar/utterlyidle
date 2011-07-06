package com.googlecode.utterlyidle.servlet;

import com.googlecode.utterlyidle.Application;
import com.googlecode.yadic.SimpleContainer;

import javax.servlet.ServletContext;

public class ApplicationContext {
    public static final String KEY = Application.class.getCanonicalName();

    public static synchronized void setApplication(ServletContext context, Application application) {
        context.setAttribute(KEY, application);
    }

    public static synchronized Application getApplication(final ServletContext servletContext, final String className) {
        if (servletContext.getAttribute(KEY) == null) {
            Application application = createApplication(servletContext, getClass(className));
            setApplication(servletContext, application);
        }
        return (Application) servletContext.getAttribute(KEY);
    }

    private static Application createApplication(ServletContext servletContext, Class<? extends Application> aClass) {
        Application application = constructApplication(aClass);
        application.add(new ServletModule(servletContext));
        return application;
    }

    private static Class<? extends Application> getClass(String className) {
        if (className == null || className.equals("")) {
            throw new UnsupportedOperationException("The web.xml must contain a servlet init-param named '" + ApplicationServlet.KEY +
                    "' with fully qualified name of a class the implements " + Application.class.getCanonicalName());
        }
        try {
            return (Class<? extends Application>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private static Application constructApplication(Class<? extends Application> aClass) {
        return new SimpleContainer().add(aClass).get(aClass);
    }
}
