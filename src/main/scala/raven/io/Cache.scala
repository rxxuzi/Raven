package raven.io

import com.google.gson.{Gson, GsonBuilder}
import raven.Raven

import java.security.MessageDigest

class Cache(val url : String, val hash : String) {
  val time : Long = System.currentTimeMillis()

  def toJson: String = {
    val gson = new Gson()
    gson.toJson(this)
  }
  private def toPrettyJson: String = {
    val gson = new GsonBuilder().setPrettyPrinting().create()
    gson.toJson(this)
  }
  override def toString: String = toPrettyJson
}

object Cache {
  def apply(raven : Raven) : Cache = new Cache(raven.url.toString, hash(raven.html))
  def apply(json : String) : Cache = new Gson().fromJson(json, classOf[Cache])

  private def hash(input: String): String = {
    val md = MessageDigest.getInstance("SHA-256")
    val hashBytes = md.digest(input.getBytes("UTF-8"))
    hashBytes.map("%02x".format(_)).mkString
  }
}
