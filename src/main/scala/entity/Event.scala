package entity

final case class Event(date: String, function: String, order: Order)
final case class Transfers(transfers: Seq[Order])

final case class TransferStatusEvent(value: String)

final case class TransferUpdateEvent(transfer: Order, newStatus: TransferStatus)
final case class TransferDeleteEvent(transfer: Order)

object Event {
  final case class ActionPerformed(description: String)
  final case class CreateEvent(event:Event)
}