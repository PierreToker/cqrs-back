package entity

final case class Event(date: String, function: String, errorDuringExecution : Boolean, order: Order)
final case class Events(Events: Seq[Event])

object Event {
  final case class ActionPerformed(description: String)
  final case class CreateEvent(event:Event)
}