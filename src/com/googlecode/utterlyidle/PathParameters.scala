package org.webfabric.rest

class PathParameters extends Parameters

object PathParameters{
  def apply(pairs: (String, String)*): PathParameters = {
    val result = new PathParameters
    pairs.foreach(pair => result.add(pair._1, pair._2))
    result
  }
}