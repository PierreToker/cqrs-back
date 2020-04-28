package service

import entity.{Order, OrderStatus}

object SynchronizeService {
  var orderStatus: Seq[OrderStatus] = DatabaseService.getOrderStatus()

  /**
   *
   */
  def synchronize() = {

  }

  /**
   * Replay every action made on an order
   * @param order
   * @param function
   */
  def replayOrder(order: Order, function: String): Boolean = {
    println("Starting the restoration process")
    val saveInitOrder = DatabaseService.getTransferById(order.id)
    println("Delete init order")
    DatabaseService.deleteOrder(order.id)
    println("get events")
    val events = DatabaseEventService.getEventsOrder(order.id)
    events.foreach(println)
    events.foreach(event =>
      if (AnalysisService.analysis(event.order, event.function)) {
        println("Error during the restoration process. The initial order will be restored. [Operation Aborted]")
        DatabaseService.deleteOrder(order.id)
        DatabaseService.createOrder(saveInitOrder)
        true
      }
    )
    false
  }
}


