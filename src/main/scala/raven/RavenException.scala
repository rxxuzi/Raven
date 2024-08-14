package raven

class RavenException(
                      message: String,
                      cause: Throwable = null,
                      val statusCode: Option[Int] = None,
                      val operation: Option[String] = None
                    ) extends Exception(message, cause) {
  override def getMessage: String = {
    val statusMsg = statusCode.map(code => s"Status Code: $code").getOrElse("")
    val operationMsg = operation.map(op => s"Operation: $op").getOrElse("")
    s"$message $statusMsg $operationMsg".trim
  }

  override def toString: String = getMessage

}

object RavenException {
  def apply(message: String): RavenException = new RavenException(message)
  def apply(message: String, cause: Throwable): RavenException = new RavenException(message, cause)
  def apply(message: String, statusCode: Int, operation: String): RavenException = {
    new RavenException(message, null, Some(statusCode), Some(operation))
  }
  def apply(message: String, cause: Throwable, statusCode: Int, operation: String): RavenException = {
    new RavenException(message, cause, Some(statusCode), Some(operation))
  }
}
