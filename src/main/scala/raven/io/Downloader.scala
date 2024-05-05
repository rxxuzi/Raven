package raven.io

import java.io.{FileOutputStream, IOException, InputStream}
import java.net.{HttpURLConnection, URL}
import scala.concurrent.{Future, ExecutionContext}
import scala.util.Using

class Downloader(val url: String) {
  private var bufferSize = 2048

  implicit val httpUrlConnectionReleasable: Using.Releasable[HttpURLConnection] = (conn: HttpURLConnection) => conn.disconnect()
  implicit val ec: ExecutionContext = ExecutionContext.global

  private def connect: HttpURLConnection = {
    val connection = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
    connection.setRequestMethod("GET")
    connection.setDoInput(true)
    connection
  }

  def setBufferSize(size: Int): Unit = {
    this.bufferSize = size
  }

  def getMediaSize: Long = {
    Using.resource(connect) { connection =>
      connection.connect()
      val length = connection.getContentLengthLong
      if (length == -1L) {
        throw new IOException("Failed to get content length.")
      }
      connection.disconnect()
      length
    }
  }

  def downloadMedia(filepath: String): Unit = {
    Using.resources(connect, connect.getInputStream, new FileOutputStream(filepath)) {
      case (connection, is, os) =>
        val buffer = new Array[Byte](bufferSize)
        LazyList.continually(is.read(buffer)).takeWhile(_ != -1).foreach { bytesRead =>
          os.write(buffer, 0, bytesRead)
        }
    }
  }

  def downloadMediaAsync(filepath: String): Future[Unit] = Future {
    downloadMedia(filepath)
  }
}


