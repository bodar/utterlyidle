package org.webfabric.rest

class MethodMatcher(method:String) extends Matcher[Request] {
  def isMatch(request: Request) = method.equals(request.method)
}