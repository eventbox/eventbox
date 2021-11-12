package eventbox.events

import java.time.LocalDateTime

private [eventbox] case class EventEnd(
  event:Event,
  maybeResponse:EventResponse,
  endDate:LocalDateTime = LocalDateTime.now
)