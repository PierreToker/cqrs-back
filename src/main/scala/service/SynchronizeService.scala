package service

import entity.Order

object SynchronizeService {

  /**
   * Check if object 'order' from db and event store have the same values.
   */
  def synchronize(orderId: Option[Int]): Boolean = {
    println(s"[--Sync order initiated--]")
    val orderDB: Order = DatabaseService.getOrderById(orderId.getOrElse(0))
    val event = DatabaseEventService.getLastEventInsertedOnOrder(orderId.getOrElse(0))
    val orderEvent: Order = new Order(event.order.id,event.order.shippingDate,event.order.destinationAddress,event.order.customerName,event.order.status)
    if (orderDB.id.equals(orderEvent.id) && orderDB.customerName.equals(orderEvent.customerName) && orderDB.status.equals(orderEvent.status) && orderDB.destinationAddress.equals(orderEvent.destinationAddress) && orderDB.shippingDate.equals(orderEvent.shippingDate)){
      println(s"[--Order from db and event are synchronized.--]")
      false
    }else{
      println(s"[--Order form db does not correspond with the order from the event store.--]")
      true
    }
  }

  /**
   * Replay every action made on an order
   * @param orderId Id of the order to restore
   */
  def replayOrder(orderId: Int): Boolean = {
    println(s"[--Starting the restoration process--]")
    val saveInitOrder:Order = DatabaseService.getOrderById(orderId)
    saveInitOrder match {
      case _:Order => {
        println(s"[--Delete init order--]")
        DatabaseService.deleteOrder(orderId)
      }
      case _ => println(s"[--No order found with that id. Process will continue.--]")
    }
    println(s"[--Get events--]")
    val events = DatabaseEventService.getEventsOrder(orderId)
    println(s"[--Restoration process in progress...--]")
    for (event <- events){
      print(event)
      if (AnalysisService.analysis(event.order, event.function,true)) {
        println(s"[--Error during the restoration process--]")
        DatabaseService.deleteOrder(orderId)
        if (saveInitOrder.isInstanceOf[Order]) {
          println(s"[--The initial order will be restored. [Operation Aborted]--]")
          DatabaseService.createOrder(saveInitOrder)
        }
        println(s"[ERROR]")
        true
      }
      println(s"[DONE]")
    }
    println(s"[--Restoration process done with no error.--]")
    false
  }
}


