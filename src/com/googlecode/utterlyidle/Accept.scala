package org.webfabric.rest

import org.webfabric.rest.Accept.{MediaRange}
import org.webfabric.regex.{Regex}
import org.webfabric.collections.Iterable

class Accept(mediaRanges: Iterable[MediaRange]) {
  def contains(value: String) = mediaRanges.exists(_.value == value)

  def quality(value: String) = mediaRanges.find(_.value == value).get.quality
}

object Accept {
  val regex = new Regex("""([^,;\s]+)(;\s*q=([0-9\.]+))?,?""")

  case class MediaRange(value: String, quality: Float)

  def apply(header: String): Accept = {
    var mediaRanges = regex.matches(header).map(m => {
      var quality = m.groups.get(3).value match {
        case null => 1.0f
        case i: String => i.toFloat
      }
      MediaRange(m.groups.get(1).value, quality)
    })
    new Accept(mediaRanges)
  }
}