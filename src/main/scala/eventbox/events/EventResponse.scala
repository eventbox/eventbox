package eventbox.events

trait EventResponse

case class EventSuccess (
  result:Any
) extends EventResponse

case class EventError (
  t:Throwable
) extends EventResponse