package eventbox.events

case class EventDone(
  event:Event,
  maybeResponse:EventResponse
)