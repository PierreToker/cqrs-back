package entity

final case class Order(id: Int, shippingDate: String, destinationAddress: String, customerName: String, status: String)
final case class Orders(transfers: Seq[Order])
final case class TransferStatus(value: String)

final case class TransferUpdate(transfer: Order, newStatus: TransferStatus)
final case class TransferDelete(transfer: Order)

object Order {
  final case class ActionPerformed(description: String)
  final case class CreateOrder(transfer: Order)
  final case class UpdateTransferStatus(id: Int, status: TransferStatus)
  final case class DeleteOrder(id: Int)
}