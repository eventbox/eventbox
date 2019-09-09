package eventbox.events

import java.time.LocalDateTime

import scala.reflect.ClassTag

case class EventDone (
  ev:Event,
  results:List[Any]=Nil,
  errors:List[EventError]=Nil,
  childEvents:List[EventDone]=Nil,
  creationDate:LocalDateTime=LocalDateTime.now
) {

  lazy val isSuccess = errors.isEmpty
  lazy val isError = !isSuccess

  /**
    * find the first result of class type in results
    * @param tag
    * @tparam T
    * @return
    */
  def find[T](implicit tag: ClassTag[T]) = results.collectFirst { case r:T => r }
}
