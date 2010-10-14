package org.webfabric.rest

import org.hamcrest.CoreMatchers._
import org.junit.Assert._
import org.junit._

class AcceptTest{
  @Test
  def canParseRealWorldAccepts() {
    val accept = Accept("""application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5""")

    assertThat(accept.contains("text/html"), is(true))
    assertThat(accept.quality("text/html"), is(0.9f))
  }

  @Test
  def canHandleWildcard() {
    val accept = Accept("""application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5""")

    assertThat(accept.contains("*/*"), is(true))
    assertThat(accept.quality("*/*"), is(0.5f))
  }

  @Test
  def canHandleWildcardWithExtraSpaceAndNoLeadingZero() {
    val accept = Accept("""text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2""")

    assertThat(accept.contains("*/*"), is(true))
    assertThat(accept.quality("*/*"), is(0.2f))
  }

}