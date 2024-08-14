package raven

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements
import raven.element.*
import raven.html.JS
import raven.io.*
import raven.util.Elem

import java.net.URL
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*
/**
 * <h1>Raven</h1>
 * This class serves as an entry point for parsing HTML content and supports various operations
 * like fetching images, videos, and other media types from the web page.
 *
 * @param url URL of the page
 * @param html HTML of the page
 * @author rxxuzi
 */
final class Raven(val url : URL, val html: String, origin : Boolean = true) {
  def this(url: URL) = {
    this(url, OpenHTML(url))
  }
  
  val doc : Document = Jsoup.parse(html)

  def get(query: String, by: By, doc : Document): Elements = {
    by match {
      case By.TAG => doc.getElementsByTag(query)
      case By.CLASS => doc.getElementsByClass(query)
      case By.ID =>
        val element: Element = doc.getElementById(query)
        val elements: Elements = new Elements()
        if (element != null) elements.add(element)
        elements
      case By.ATTR => doc.getElementsByAttribute(query)
    }
  }

  def get(query: String, by: By): Elements = get(query, by, this.doc)
  def get(query: String, by: By, elements: Elements): Elements = {
    val doc = Jsoup.parse(elements.html)
    get(query, by, doc)
  }

  def getAllImages: List[Img] = {
    val images: Elements = doc.select("img")
    images.toArray.map(_.asInstanceOf[Element]).map(new Img(url,_)).toList
  }

  def getImages(query: String, by: By): List[Img] = {
    val elements = get(query, by)
    val imgElements = if (elements.isEmpty) doc.select("img") else elements.select("img")
    imgElements.asScala.map(new Img(url, _)).toList
  }

  def getAllVideo: List[Vid] = {
    val videos: Elements = doc.select("video")
    videos.toArray.map(_.asInstanceOf[Element]).map(new Vid(url, _)).toList
  }

  def getVideos(query: String, by: By): List[Vid] = {
    val videos = get("video", By.TAG, get(query, by))
    videos.toArray.map(_.asInstanceOf[Element]).map(new Vid(url, _)).toList
  }

  def getAllAudio: List[Aud] = {
    val audios: Elements = doc.select("audio")
    audios.toArray.map(_.asInstanceOf[Element]).map(new Aud(url, _)).toList
  }

  def getAudios(query: String, by: By): List[Aud] = {
    val audios = get("audio", By.TAG, get(query, by))
    audios.toArray.map(_.asInstanceOf[Element]).map(new Aud(url, _)).toList
  }

  def getAllURL: List[URL] = {
    val links: Elements = doc.select("a")
    Elem.extractUrlsFromLinks(url, links)
  }

  def getURLs(query: String, by: By): List[URL] = {
    val links = get(query, by).select("a")

    links.toArray.map(_.asInstanceOf[Element]).flatMap { link =>
      val href = link.attr("href")
      Elem.fullURL(this.url, href)
    }.toList
  }

  def save(path : String) : Unit = {
    val body = doc.body
    Writer.write(path, body.html)
  }
  
  def snippet(name : String , by : By) : Raven = {
    val body = doc.body
    val elements = get(name, by)
    new Raven(url, elements.html(), origin = false)
  }

  def cut(name: String, by: By): Unit = {
    by match {
      case By.TAG => doc.getElementsByTag(name).remove()
      case By.CLASS => doc.getElementsByClass(name).remove()
      case By.ID => Option(doc.getElementById(name)).foreach(_.remove())
      case By.ATTR => doc.getElementsByAttribute(name).remove()
    }
  }

  def parse(query: String): List[Element] = {
    val elements: Elements = doc.select(query)
    elements.toArray.map(_.asInstanceOf[Element]).toList
  }

  def text(): String = {
    doc.text()
  }
  
  def js(): List[JS] = {
    val scripts: Elements = get("script", By.TAG)
    val js = ListBuffer[JS]()

    scripts.forEach { element =>
      if (Elem.containsResourceAttr(element)) {
        js += JS(url, element, isExternal = true)
      } else if (element.data().nonEmpty) {
        js += JS(url, element, isExternal = false)
      }
    }

    js.toList
  }

  def cache() : Cache = Cache(this)

  override def toString: String = s"Raven($url)"
}

object Raven {
  def apply(url : URL) = new Raven(url)
  def apply(url : String) = new Raven(new URL(url))
  def apply(url : URL, html : String) = new Raven(url, html)
}
