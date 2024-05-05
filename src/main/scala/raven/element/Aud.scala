package raven.element

import org.jsoup.nodes.Element

import java.net.URL

class Aud(val baseurl: URL, val element: Element) extends Media {
  val filesize: Long = downloader.getMediaSize

  override def toString: String = s"Audio(LINK: $src, FILE: $filename)"
}
