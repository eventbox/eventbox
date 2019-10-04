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
  def find[T](implicit tag: ClassTag[T]): Option[T] = results.collectFirst { case r:T => r }
  def filter[T](implicit tag: ClassTag[T]): List[T] = results.collect { case r:T => r }

  def findChildEvent[T<:Event](implicit tag: ClassTag[T]): Option[T] = childEvents.map(_.ev).collectFirst { case r:T => r }
  def filterChildEvent[T<:Event](implicit tag: ClassTag[T]): List[T] = childEvents.map(_.ev).collect { case r:T => r }
  def existsChildEvent[T<:Event](implicit tag: ClassTag[T]): Boolean = findChildEvent[T].nonEmpty
}
