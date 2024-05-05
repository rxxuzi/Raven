package raven.io

import java.io.*
import scala.io.Source
import scala.util.Using

class Reader(val path: String){
  val file : File = new File(path)
  if (!file.exists()) throw new FileNotFoundException(s"File not found: $path")

  def read(): String = {
    Using(Source.fromFile(file))(_.mkString).getOrElse("I/O Error")
  }

  def readLine(line: Int): Option[String] = {
    Using(Source.fromFile(file)) { source =>
      source.getLines().drop(line).nextOption()
    }.getOrElse(None)
  }

  def readBytes(): Array[Byte] = {
    Using(new FileInputStream(file)) { inputStream =>
      val bytes = new Array[Byte](inputStream.available())  // Not recommended for large files
      inputStream.read(bytes)
      bytes
    }.getOrElse(Array.empty[Byte])
  }
}

object Reader {
  def apply(path: String): Reader = new Reader(path)
  def apply(file: File): Reader = new Reader(file.getPath)
  
  def read(path: String): String = apply(path).read()
  def read(file: File): String = apply(file).read()
}