package eventbox.events

import java.util.UUID

import akka.actor.ActorRef

trait Event {
  val eventCtx:EventCtx
  var sender:Option[ActorRef]=None

  private[eventbox] var childEvents = List.empty[Event]
  private[eventbox] var actorResponses = Map.empty[ActorRef, EventResponse]

}

case class EventCtx (
  id:UUID=UUID.randomUUID(),
  parentId:Option[UUID]=None,
  originalId:Option[UUID]=None
){

  def copyCtx = {
    EventCtx(
      id = UUID.randomUUID(),
      parentId = Some(this.id),
      originalId = this.originalId.orElse(Some(this.id))
    )
  }
}
