package entity

final case class Event(id:Int, date: String, function: String, errorDuringExecution : Boolean, order: Order)
final case class Events(Events: Seq[Event])

object Event {
  final case class ActionPerformed(description: String)
  final case class CreateEvent(event:Event)
}