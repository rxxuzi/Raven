package raven.element

import org.jsoup.nodes.Element
import java.net.URL

class Vid(val baseurl: URL, val element: Element) extends Media {
  val filesize: Long = downloader.getMediaSize

  override def toString: String = s"Vid(LINK: $src, FILE: $filename)"
}
