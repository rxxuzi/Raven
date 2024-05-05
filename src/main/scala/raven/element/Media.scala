package raven.element

import org.jsoup.nodes.Element
import raven.io.Downloader
import scala.jdk.CollectionConverters._
import java.net.URL

trait Media {
  val baseurl: URL
  val element: Element
  val staticDomain: String = s"${baseurl.getProtocol}://${baseurl.getHost}"
  val isStatic: Boolean = element.attr("src").startsWith("http")
  val src: String = {
    val rawSrc = element.attr("src")
    if (rawSrc.startsWith("http")) {
      rawSrc
    } else if (rawSrc.startsWith("//")) {
      s"${baseurl.getProtocol}:$rawSrc"
    } else {
      staticDomain + rawSrc
    }
  }
  val downloader = new Downloader(src)
  val classSet : Set[String] = element.classNames().asScala.toSet
  val id : String = element.id()
  val attributes : Map[String, String] = element.attributes().asList().asScala.map(attr => (attr.getKey, attr.getValue)).toMap
  val filename: String = src.split("/").last

  def download(path: String): Unit = {
    downloader.downloadMedia(path)
  }

  def download(): Unit = {
    download(filename)
  }
}
