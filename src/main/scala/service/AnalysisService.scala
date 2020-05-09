package service

import entity.{Event, Order, OrderStatus}

object AnalysisService {
  var orderStatus: Seq[OrderStatus] = DatabaseService.getOrderStatus()

  def analysis(OrderCommand: Order, function: String, noNeedId:Boolean = false): Boolean = {
    if (function.equals("OrderCreated")){
      var newOrder: Order = OrderCommand
      if (noNeedId == false) {
        val idAvailable: Option[Int] = DatabaseEventService.getFreeId
        newOrder = newOrder.copy(id = idAvailable)
      }
      DatabaseService.createOrder(newOrder)
      val event:Event = DatabaseEventService.insertEvent(function, newOrder)
      DatabaseEventService.updateEventError(event.id,false)
      false
    }else{
      val databaseOrder:Order = DatabaseService.getOrderById(OrderCommand.id.getOrElse(0))
      if (!databaseOrder.equals(None)) {
        function match {
          case "OrderStatusUpdatedToNextStep" => {
            if (!orderStatus.find(_.id.toString == OrderCommand.status).isDefined) {
              println(new NoSuchElementException("Status n° " + OrderCommand.status + " unknow."))
              return true
            }
            if (databaseOrder.status >= OrderCommand.status) {
              println(new IllegalStateException("An order cannot rewind (or stay) his status through this way."))
              true
            } else {
              var dbOrderIncreased: Int = databaseOrder.status.toInt
              dbOrderIncreased += 1
              if (OrderCommand.status.toInt != dbOrderIncreased) {
                println(new IllegalStateException("An order can only evolve by one step over."))
                true
              } else {
                DatabaseService.updateOrderStatus(OrderCommand)
                false
              }
            }
          }

          case "OrderDeleted" => {
            DatabaseService.deleteOrder(OrderCommand.id.getOrElse(0))
            false
          }

          case _ => {
            println(new NoSuchElementException("Unknow command."))
            true
          }
        }
      }else{
        println(s"Order n° {} does not exist on database. Action {} aborted.",OrderCommand.id, function)
        true
      }
    }
  }

}
