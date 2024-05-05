package raven.element

import org.jsoup.nodes.Element
import java.net.URL

class Img(val baseurl: URL, val element: Element) extends Media {
  val alt: String = element.attr("alt")
  val filesize: Long = downloader.getMediaSize

  override def toString: String = s"Img(LINK: $src, ALT: $alt, FILE: $filename)"
}
