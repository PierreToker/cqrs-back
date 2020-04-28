package entity

final case class Order(id: Int, shippingDate: String, destinationAddress: String, customerName: String, status: String)
final case class Orders(orders: Seq[Order])
final case class OrderStatus(value: String)

object Order {
  final case class ActionPerformed(description: String)
  final case class CreateOrder(order: Order)
}