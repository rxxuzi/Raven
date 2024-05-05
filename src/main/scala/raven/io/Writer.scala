package raven.io

import java.io._
import scala.util.Using

class Writer(val path: String) {
  val file = new File(path)

  def write(data: String): Unit = {
    Using.resource(new BufferedWriter(new FileWriter(file))) { bw =>
      bw.write(data)
    }
  }

  def write(data: Array[Byte]): Unit = {
    Using.resource(new BufferedOutputStream(new FileOutputStream(file))) { bw =>
      bw.write(data)
      bw.flush()
    }
  }

  def append(data: String): Unit = {
    Using.resource(new BufferedWriter(new FileWriter(file, true))) { bw =>
      bw.write(data)
    }
  }

  def append(data: Array[Byte]): Unit = {
    Using.resource(new BufferedOutputStream(new FileOutputStream(file, true))) { bw =>
      bw.write(data)
      bw.flush()
    }
  }

  def exists: Boolean = file.exists()

  def delete: Boolean = file.delete()

}

object Writer {
  def apply(path: String): Writer = new Writer(path)

  def write(path: String, data: String): Unit = new Writer(path).write(data)
  def write(path: String, data: Array[Byte]): Unit = new Writer(path).write(data)

  def append(path: String, data: String): Unit = new Writer(path).append(data)
  def append(path: String, data: Array[Byte]): Unit = new Writer(path).append(data)
}
