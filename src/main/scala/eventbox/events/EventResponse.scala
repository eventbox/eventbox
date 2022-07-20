package eventbox.events

import java.time.LocalDateTime

trait EventResponse {
  val startDate:LocalDateTime
  val endDate:LocalDateTime = LocalDateTime.now
}

case class EventSuccess (
  result:Any,
  startDate:LocalDateTime,
) extends EventResponse

case class EventError (
  t:Throwable,
  startDate:LocalDateTime
) extends EventResponse