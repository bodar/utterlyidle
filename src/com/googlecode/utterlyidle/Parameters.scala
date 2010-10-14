package org.webfabric.rest

import org.webfabric.collections.{Map, List, Iterable}
import org.webfabric.collections.Map.toIterable

class Parameters extends Iterable[(String, List[String])]{
  val values = Map[String, List[String]]()

  def add(name:String, value:String):Parameters = {
    if(!values.containsKey(name)) {
      values.put(name, List[String]())
    }
    values.get(name).add(value)
    this
  }

  def size = values.size

  def getValue(name:String):String = {
    if(!values.containsKey(name)) null else values.get(name).get(0)
  }

  def contains(name:String):Boolean = {
    values.containsKey(name)
  }

  def iterator = values.iterator
}