package raven.element

import org.jsoup.nodes.Element
import raven.io.Downloader
import scala.jdk.CollectionConverters._
import java.net.URL
import raven.util.Elem

trait Media {
  val baseurl: URL
  val element: Element
  val staticDomain: String = Elem.getStaticResources(baseurl)
  val isStatic: Boolean = Elem.isStaticResource(element)
  val src: String = Elem.fullURL(baseurl, element).get.toString
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
