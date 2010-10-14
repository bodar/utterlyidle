package org.webfabric.rest

class PathMatcher(uriTemplate:UriTemplate) extends Matcher[Request] {
  def isMatch(request: Request) = uriTemplate.isMatch(removeLeadingSlash(request.path))

  def removeLeadingSlash(path:String):String = {
    path.replaceFirst("^/", "")
  }
}