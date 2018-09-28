package eventbox

import akka.actor.{Actor, ActorLogging, ActorRef}
import eventbox.events._

abstract class EventManager extends Actor with ActorLogging {

  var actors = List.empty[ActorRef]
  var eventQ = List.empty[Event]

  def onEvent(msg:Event)

  override def receive: Receive = {

    case ActorJoin =>
      actors :+= sender

    case EventDone(msg, eventResponse) => {

      log.info("EventDone:" + msg)

      val id = msg.eventCtx.originalId.getOrElse(msg.eventCtx.id)
      val maybeOriginalEvent = eventQ.find(_.eventCtx.id == id)

      maybeOriginalEvent.foreach { ev =>

        val events = ev +: ev.childEvents
        //println("events:" + events)

        events.find(_.eventCtx.id == msg.eventCtx.id).foreach { e =>

          e.actorResponses = e.actorResponses + (sender -> eventResponse)
        }

        // check ques tous les events sont ok
        if (events.forall(_.actorResponses.size == actors.size)){

          val results: List[Any] = ev.actorResponses.collect { case (_, e:EventSuccess) => e.result }.toList

          val errors: List[EventError] = for {
            e <- events
            error <- e.actorResponses.collect { case (_, e:EventError) => e}
          } yield error

          log.info("EventFinish:" + ev)
          ev.sender.foreach(_ ! EventFinish(results, errors))

          // remove this event from eventQueue
          eventQ = eventQ.filterNot(_ == ev)
        }

      }

    }
    case msg:Event => {

      log.info("Event:" + msg)

      if (eventQ.exists(_.eventCtx.id == msg.eventCtx.id) || eventQ.exists(_.childEvents.exists(_.eventCtx.id == msg.eventCtx.id))){
        log.error(
          s"""
             |Bad EventCtx. An event already exist with this id: ${msg.eventCtx.id}
             |Make sure using copyCtx() method on this message:
             |${msg}
               """.stripMargin
        )
      } else {

        try {
          onEvent(msg)
        } catch {
          case t:Throwable =>
        }

        msg.eventCtx.originalId match {
          case None =>
            eventQ :+= msg

          case Some(uuid) =>
            eventQ.find(_.eventCtx.id == uuid).foreach { ev =>
              ev.childEvents :+= msg
            }

        }

        broadcast(msg, sender)
      }
    }
    case msg => {
      log.warning("msg unhandled: " + msg)
    }
  }

  def broadcast(msg:Event, sender:ActorRef): Unit ={
    msg.sender = Some(sender)
    actors foreach { a =>
      a ! msg
    }
  }
}