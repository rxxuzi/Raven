package raven.util

import org.jsoup.nodes.Element
import org.jsoup.select.*
import java.net.URL

object Elem {
  private val RESOURCES_ATTRS : Set[String] = Set("href", "src", "action", "poster", "srcset", "data")

  /**
   * Converts a collection of link elements into a list of URLs.
   *
   * @param origin The base URL of the page from which links are extracted.
   * @param links  Elements containing the link tags.
   * @return A list of distinct URLs extracted from the link elements.
   */
  def extractUrlsFromLinks(origin: URL, links: Elements): List[URL] = {
    links.toArray.map(_.asInstanceOf[Element]).flatMap { link =>
      val href = link.attr("href")
      href match {
        case href if href.startsWith("http") => Some(new URL(href))
        case href if href.nonEmpty => Some(new URL(origin, href))
        case _ => None
      }
    }.toSet.toList
  }

  /**
   * Constructs a full URL from a base URL and a target URL, which may be relative or protocol-relative.
   *
   * @param origin The base URL.
   * @param target The target URL, which may be relative or absolute.
   * @return A full URL as a URL object.
   */
  def fullURL(origin: URL, target: URL): URL = {
    val targetString = target.toString
    if (target.getProtocol != null) {
      target
    } else if (targetString.startsWith("//")) {
      new URL(s"${origin.getProtocol}:$targetString")
    } else {
      new URL(origin, targetString)
    }
  }

  def fullURL(origin: URL, target: String): URL = fullURL(origin, new URL(target))

  /**
   * Attempts to construct a full URL from a base URL and an HTML element containing a URL attribute.
   *
   * @param origin  The base URL.
   * @param element The HTML element containing URL-related attributes.
   * @return An Option containing the full URL if successful, None otherwise.
   */
  def fullURL(origin: URL, element: Element): Option[URL] = {
    val rawUrl = RESOURCES_ATTRS.view
      .map(attr => element.attr(attr).trim)
      .find(_.nonEmpty)

    rawUrl.flatMap { url =>
      try {
        val targetURL = new URL(origin, url)
        Some(fullURL(origin, targetURL))
      } catch {
        case e: Exception => None
      }
    }
  }
  
  def getStaticResources(url: URL): String = s"${url.getProtocol}://${url.getHost}"
  
  def isStaticResource(url: String): Boolean = url.startsWith("http") || url.startsWith("https")
  
  def isStaticResource(url: URL): Boolean = url.getProtocol == "http" || url.getProtocol == "https"
  
  def isStaticResource(element: Element): Boolean = {
    val rawUrlOption = RESOURCES_ATTRS.view
      .map(attr => element.attr(attr).trim)
      .find(_.nonEmpty)

    rawUrlOption.exists(url => url.startsWith("http") || url.startsWith("https"))
  }
}
