package eventbox

import akka.actor.{Actor, ActorLogging, ActorRef}
import eventbox.events.{EventResponse, _}

import scala.concurrent.Future
import scala.util.Try

abstract class EventManager extends Actor with ActorLogging {

  var actors = List.empty[ActorRef]
  var eventQ = List.empty[Event]

  /**
    * Event is start
    * @param msg
    */
  def onEventStart(msg:Event):Future[Any]

  /**
    * Event is ended
    * @param msg
    */
  def onEventEnd(msg:Event, ex:Option[Throwable]):Future[Any]

  /**
    * Original Event & all child events are ended
    * @param msg:Event (Original event)
    */
  def onEventDone(msg:Event):Future[Any]

  override def receive: Receive = {

    case ActorJoin =>
      actors :+= sender

    case EventEnd(msg, eventResponse) => {

      log.info("EventEnd:" + msg)

      val throwable = eventResponse match {
        case EventError(t) => Some(t)
        case _ => None
      }

      Try { onEventEnd(msg, throwable) }

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

          Try { onEventDone(ev) }

          log.info("EventDone:" + ev)

          ev.sender.foreach(_ ! buildEventDone(ev))

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

        Try { onEventStart(msg) }

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

  /*
  def ! (implicit parent:Event=NoEvent()) = {

  }
  */

  def broadcast(msg:Event, sender:ActorRef): Unit ={
    msg.sender = Some(sender)
    actors foreach { a =>
      a ! msg
    }
  }

  private def buildEventDone(ev:Event) : EventDone = {

    EventDone(
      ev=ev,
      results = ev.actorResponses.collect { case (_, e:EventSuccess) => e.result }.toList,
      errors = ev.actorResponses.collect { case (_, e:EventError) => e}.toList,
      childEvents = ev.childEvents.map(buildEventDone)
    )
  }
}