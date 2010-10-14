package org.webfabric.rest

import org.webfabric.servlet.{ContextPath, BasePath, WarRoot}
import javax.servlet.{ServletContext, ServletContextEvent, ServletContextListener}
import org.webfabric.properties.PropertiesApplication
import com.googlecode.yadic.SimpleContainer

class ApplicationStarter extends ServletContextListener{
  def createApplication(servletContext: ServletContext): Application = {
    val className = servletContext.getInitParameter(getClass.getName)
    if(className == null || className.equals("")){
      throw new UnsupportedOperationException("The web.xml must container a context-param called " + getClass.getName)
    }
    val aClass = Class.forName(className)
    val instance = new SimpleContainer().add(aClass).resolve(aClass)
    if(!instance.isInstanceOf[Application]){
      throw new UnsupportedOperationException(className + " must implement " + classOf[Application].getName )
    }
    instance.asInstanceOf[Application]
  }

  def contextInitialized(event: ServletContextEvent) = {
    val context = event.getServletContext
    val application = createApplication(context).addInstance(WarRoot(context)).addInstance(ContextPath(context))
    context.setAttribute(classOf[Application].getCanonicalName, application)
  }
  
  def contextDestroyed(event: ServletContextEvent) = {}
}