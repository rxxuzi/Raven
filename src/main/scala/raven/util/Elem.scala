package raven.util

import org.jsoup.nodes.Element
import org.jsoup.select.*

import java.net.URL

object Elem {
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
}
