package com.googlecode.utterlyidle

class MethodMatcher(method:String) extends Matcher[Request] {
  def isMatch(request: Request) = method.equals(request.method)
}