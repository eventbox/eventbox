package eventbox.events

trait EventHook

case class BeforeEvent(
  msg:Event, eventCtx: EventCtx
) extends Event with EventHook