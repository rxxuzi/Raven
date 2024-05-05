package raven

import raven.element.*
import raven.io.*
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements

import java.net.URL

/**
 * <h1>Raven</h1>
 * This class serves as an entry point for parsing HTML content and supports various operations
 * like fetching images, videos, and other media types from the web page.
 *
 * @param url URL of the page
 * @param html HTML of the page
 * @author rxxuzi
 */
class Raven(val url : URL, val html: String) {
  def this(url: URL) = {
    this(url, OpenHTML(url))
  }
  val doc : Document = Jsoup.parse(html)

  private def get(query: String, by: By): Elements = {
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

  def getAllImages: List[Img] = {
    val images: Elements = doc.select("img")
    images.toArray.map(_.asInstanceOf[Element]).map(new Img(url,_)).toList
  }

  def getImages(query: String, by: By): List[Img] = {
    val images = get("img " + query, by)
    images.toArray.map(_.asInstanceOf[Element]).map(new Img(url, _)).toList
  }

  def getAllVideo: List[Vid] = {
    val videos: Elements = doc.select("video")
    videos.toArray.map(_.asInstanceOf[Element]).map(new Vid(url, _)).toList
  }

  def getAllAudio: List[Aud] = {
    val audios: Elements = doc.select("audio")
    audios.toArray.map(_.asInstanceOf[Element]).map(new Aud(url, _)).toList
  }

  def getAllURL: List[URL] = {
    val links: Elements = doc.select("a")
    val urls = links.toArray.map(_.asInstanceOf[Element]).flatMap { link =>
      val href = link.attr("href")
      href match {
        case href if href.startsWith("http") => Some(new URL(href))
        case href if href.nonEmpty => Some(new URL(url, href))
        case _ => None
      }
    }.toSet
    urls.toList
  }

  def save(path : String) : Unit = {
    val body = doc.body
    Writer.write(path, body.html)
  }
  
  def snippet(name : String , by : By) : Raven = {
    val body = doc.body
    val elements = get(name, by)
    new Raven(url, elements.html())
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

  def cache() : Cache = Cache(this)

  override def toString: String = s"Raven($url)"
}

object Raven {
  def apply(url : URL) = new Raven(url)
  def apply(url : String) = new Raven(new URL(url))
}
