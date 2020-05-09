package entity

final case class Order(id: Option[Int] = None, shippingDate: String, destinationAddress: String, customerName: String, status: String)
final case class Orders(orders: Seq[Order])

object Order {
  final case class ActionPerformed(description: String)
  final case class CreateOrder(order: Order)
}