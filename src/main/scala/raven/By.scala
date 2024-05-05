package raven
import org.jsoup.nodes.Document
import org.jsoup.select._
enum By {
  case TAG, CLASS, ID, ATTR

  def querySelector(doc: Document, query: String): Elements = this match {
    case By.TAG => doc.getElementsByTag(query)
    case By.CLASS => doc.getElementsByClass(query)
    case By.ID => new Elements(doc.getElementById(query))
    case By.ATTR => doc.getElementsByAttribute(query)
  }
  
  override def toString: String = this match {
    case TAG => "tag"
    case CLASS => "class"
    case ID => "id"
    case ATTR => "attr"
  }
}