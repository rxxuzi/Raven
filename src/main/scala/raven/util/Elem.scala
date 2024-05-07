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
    val res = getStaticURL(origin)
    val targetString = target.toString
    if (target.getProtocol != null) {
      target
    } else if (targetString.startsWith("//")) {
      new URL(s"${res.getProtocol}:$targetString")
    } else {
      new URL(res, targetString)
    }
  }

  def fullURL(origin: URL, target: String): Option[URL] = {
    val res = getStaticURL(origin)
    try {
      val url = {
        if (target.startsWith("//")) {
          new URL(s"${res.getProtocol}:$target")
        } else if (target.startsWith("http")) {
          new URL(target)
        } else {
          new URL(res, target)
        }
      }
      Some(url)
    } catch {
      case e: Exception => None
    }

  }

  /**
   * Attempts to construct a full URL from a base URL and an HTML element containing a URL attribute.
   *
   * @param origin  The base URL.
   * @param element The HTML element containing URL-related attributes.
   * @return An Option containing the full URL if successful, None otherwise.
   */
  def fullURL(origin: URL, element: Element): Option[URL] = {
    val res = getStaticURL(origin)
    val rawUrl = RESOURCES_ATTRS.view
      .map(attr => element.attr(attr).trim)
      .find(_.nonEmpty)

    rawUrl.flatMap { url =>
      try {
        val targetURL = new URL(res, url)
        Some(fullURL(res, targetURL))
      } catch {
        case e: Exception => None
      }
    }
  }

  /**
   * Retrieves the base URL as a string containing only the protocol and host.
   *
   * @param url The URL from which to extract the base.
   * @return A string representing the base URL with protocol and host.
   */
  def getStaticResources(url: URL): String = s"${url.getProtocol}://${url.getHost}"

  /**
   * Constructs a new URL object using only the protocol and host from the given URL.
   *
   * @param url The URL from which to construct the new URL.
   * @return A new URL object containing only the protocol and host.
   */
  def getStaticURL(url: URL): URL = new URL(getStaticResources(url))

  def isStaticResource(url: String): Boolean = url.startsWith("http") || url.startsWith("https")
  
  def isStaticResource(url: URL): Boolean = url.getProtocol == "http" || url.getProtocol == "https"

  /**
   * Determines whether an HTML element contains a URL attribute that points to a static resource.
   *
   * @param element The HTML element to evaluate.
   * @return True if the element has a URL attribute beginning with "http" or "https".
   */
  def isStaticResource(element: Element): Boolean = {
    val rawUrlOption = RESOURCES_ATTRS.view
      .map(attr => element.attr(attr).trim)
      .find(_.nonEmpty)

    rawUrlOption.exists(url => isStaticResource(url))
  }

  /**
   * Checks whether an HTML element contains any attributes that are URL-related.
   *
   * @param element The HTML element to check.
   * @return True if the element contains any URL-related attributes.
   */
  def containsResourceAttr(element: Element): Boolean = {
    val rawUrlOption = RESOURCES_ATTRS.view
      .map(attr => element.attr(attr).trim)
      .find(_.nonEmpty)
    rawUrlOption.isDefined
  }
}
