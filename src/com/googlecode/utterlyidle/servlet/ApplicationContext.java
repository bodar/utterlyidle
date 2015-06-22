package com.googlecode.utterlyidle.servlet;

import com.googlecode.totallylazy.Block;
import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Callers;
import com.googlecode.totallylazy.Runnables;
import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.UtterlyIdleProperties;
import com.googlecode.utterlyidle.modules.Modules;
import com.googlecode.utterlyidle.services.Service;
import com.googlecode.yadic.SimpleContainer;

import javax.servlet.ServletContext;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Unchecked.cast;
import static com.googlecode.utterlyidle.servlet.ServletApiWrapper.basePath;

public class ApplicationContext {
    public static final String KEY = Application.class.getCanonicalName();

    public static synchronized void setApplication(ServletContext context, Application application) {
        context.setAttribute(KEY, application);
    }

    public static synchronized void removeApplication(ServletContext context) {
        context.setAttribute(KEY, null);
    }

    public static synchronized Application getApplication(final ServletContext servletContext, final String className) {
        return getApplication(servletContext, className, (Block<Application>) application -> {
            if (Modules.autoStart(application.applicationScope().get(UtterlyIdleProperties.class))) {
                Service.functions.start().callConcurrently(application);
            }
        });
    }

    public static synchronized Application getApplication(final ServletContext servletContext, final String className, boolean start) {
        return getApplication(servletContext, className, start ? Service.functions.start() : Runnables.<Service>doNothing());
    }

    private static synchronized Application getApplication(final ServletContext servletContext, final String className, Function1<? super Application, ?> onStart) {
        if (servletContext.getAttribute(KEY) == null) {
            Application application = createApplication(servletContext, getClass(className));
            Callers.call(onStart, application);
            setApplication(servletContext, application);
        }
        return (Application) servletContext.getAttribute(KEY);
    }

    private static Application createApplication(ServletContext servletContext, Class<? extends Application> aClass) {
        Application application = constructApplication(aClass, basePath(servletContext));
        application.add(new ServletModule(servletContext));
        return application;
    }

    private static Class<? extends Application> getClass(String className) {
        if (className == null || className.equals("")) {
            throw new UnsupportedOperationException("The web.xml must contain a servlet init-param named '" + ApplicationServlet.KEY +
                    "' with fully qualified name of a class the implements " + Application.class.getCanonicalName());
        }
        try {
            return cast(Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private static Application constructApplication(Class<? extends Application> aClass, Callable<BasePath> basePathActivator) {
        return new SimpleContainer().addActivator(BasePath.class, basePathActivator).add(aClass).get(aClass);
    }
}
