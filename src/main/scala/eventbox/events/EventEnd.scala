package eventbox.events

private [eventbox] case class EventEnd(
  event:Event,
  maybeResponse:EventResponse
)