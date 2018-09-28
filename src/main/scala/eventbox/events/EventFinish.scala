package eventbox.events

import scala.reflect.ClassTag


case class EventFinish (
  results:List[Any]=Nil,
  errors:List[EventError]=Nil
) {
  lazy val isSuccess = errors.isEmpty
  lazy val isError = !isSuccess

  /**
    * find the first result of class type in results
    * @param tag
    * @tparam T
    * @return
    */
  def find[T](implicit tag: ClassTag[T]) = results.collectFirst { case r:T => r}
}

trait EventResponse

case class EventSuccess (
  result:Any
) extends EventResponse

case class EventError (
  t:Throwable
) extends EventResponse
