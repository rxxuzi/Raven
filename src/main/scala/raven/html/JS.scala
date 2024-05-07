package raven.html
import java.net.URL
import org.jsoup.nodes.Element
import raven.*
import raven.util.*
import raven.io.*

class JS (val url : URL, val element: Element, val isExternal: Boolean){
  private val isStatic: Boolean = Elem.isStaticResource(element)

  val text: String = if (isExternal) {
    val src: URL = if (isStatic) url else Elem.fullURL(url, element).get
    val res: String = src.getFile
    OpenHTML(src)
  } else {
    element.data()
  }

  def save(path : String) : Unit = {
    Writer.write(path, text)
  }
  override def toString: String = s"JS(URL : $url, External : $isExternal)"
}

object JS {
  def apply(url: URL, element: Element, isExternal : Boolean): JS = {
    new JS(url, element, isExternal)
  }
}
