package raven

import java.io.{BufferedReader, InputStreamReader}
import java.net.{HttpURLConnection, URL}
import scala.util.{Try, Using}

class OpenHTML(val url: URL) {
  implicit val httpURLConnectionReleasable: Using.Releasable[HttpURLConnection] = (resource: HttpURLConnection) => resource.disconnect()

  private def getPage: Try[String] = {
    Using.Manager { use =>
      val connection = use(OpenHTML.connect(url))
      connection.connect()
      val status = connection.getResponseCode
      if (status != HttpURLConnection.HTTP_OK) {
        throw new Exception(s"Error: $status")
      } else {
        val inputStream = use(connection.getInputStream)
        val reader = use(new BufferedReader(new InputStreamReader(inputStream)))
        Iterator.continually(reader.readLine()).takeWhile(_ != null).mkString("\n")
      }
    }
  }
}

object OpenHTML {
  def apply(url: URL): String = {
    new OpenHTML(url).getPage.getOrElse(throw new Exception("Failed to retrieve the page"))
  }

  def apply(url: String): String = apply(new URL(url))

  def connect(url: URL): HttpURLConnection = {
    val connection = url.openConnection().asInstanceOf[HttpURLConnection]
    connection.setAllowUserInteraction(false)
    connection.setInstanceFollowRedirects(true)
    connection.setRequestMethod("GET")
    connection
  }
}

