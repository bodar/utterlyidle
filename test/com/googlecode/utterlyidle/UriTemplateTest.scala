package org.webfabric.rest

import org.hamcrest.CoreMatchers._
import org.junit.Assert._
import org.junit._

class UriTemplateTest{
  @Test
  def supportedMultiplePathParams() {
    val template = new UriTemplate("""properties/{id}/{name}""")
    Console.println(template.templateRegex)
    assertThat(template.isMatch("properties/123/bob"), is(true))
    val pathParameters = template.extract("properties/123/bob")
    assertThat(pathParameters.getValue("id"), is("123"))
    assertThat(pathParameters.getValue("name"), is("bob"))
    assertThat(template.generate(PathParameters("id" -> "123", "name" -> "bob")), is("properties/123/bob"))
  }

  @Test
  def canCaptureEnd() {
    val template = new UriTemplate("""path/{id}""")
    assertThat(template.isMatch("path/123/someotherpath"), is(true))
    assertThat(template.extract("path/123/someotherpath").getValue("$"), is("/someotherpath"))
    assertThat(template.isMatch("path/123"), is(true))
    assertThat(template.generate(PathParameters("id" -> "123", "$" -> "/someotherpath")), is("path/123/someotherpath"))
  }

  @Test
  def supportsCustomRegex() {
    val template = new UriTemplate("""path/{id:\d}""")
    assertThat(template.isMatch("path/foo"), is(false))
    assertThat(template.isMatch("path/1"), is(true))
    assertThat(template.extract("path/1").getValue("id"), is("1"))
  }

  @Test
  def canMatch() {
    val template = new UriTemplate("""path/{id}""")
    assertThat(template.isMatch("path/foo"), is(true))
  }

  @Test
  def canExtractFromUri() {
    val template = new UriTemplate("""path/{id}""")
    assertThat(template.extract("path/foo").getValue("id"), is("foo"))
  }

  @Test
  def canGenerateUri() {
    val template = new UriTemplate("path/{id}")
    assertThat(template.generate(PathParameters("id" -> "foo")), is("path/foo"))
  }

}
