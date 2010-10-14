package org.webfabric.rest

import org.webfabric.collections.List
import com.googlecode.yadic.{Container, SimpleContainer}

class RestApplication() extends Application {
  lazy val engine = applicationScope.resolveType(classOf[RestEngine])
  val applicationScope = new SimpleContainer
  val modules = List[Module]()

  add(new CoreModule)

  def createRequestScope: Container = {
    val requestScope = new SimpleContainer(applicationScope)
    modules.foreach(_.addPerRequestObjects(requestScope))
    requestScope
  }

  def handle(request: Request, response: Response) = {
    engine.handle(createRequestScope, request, response)
  }

  def add (module: Module):Application = {
    module.addPerApplicationObjects(applicationScope)
    module.addResources(engine)
    modules.add(module)
    this
  }

  def addInstance (instance: Object):Application = {
    applicationScope.remove(instance.getClass)
    applicationScope.addInstance(instance)
    this
  }
}