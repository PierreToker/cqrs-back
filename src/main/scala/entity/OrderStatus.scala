package entity

final case class OrderStatus(id: Int, description: String)

object OrderStatus {
  final case class ActionPerformed(description: String)

}