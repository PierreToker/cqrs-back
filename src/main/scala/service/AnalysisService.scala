package service

import entity.{Order, OrderStatus}

object AnalysisService {
  var orderStatus: Seq[OrderStatus] = DatabaseService.getOrderStatus()

  def analysis(OrderCommand: Order, function: String): Boolean = {
    if (function.equals("OrderCreated")){
      DatabaseService.createOrder(OrderCommand)
      false
    }else{
      val databaseOrder = DatabaseService.getTransferById(OrderCommand.id)
      if (databaseOrder != None) {
        function match {
          case "OrderStatusUpdatedToNextStep" => {
            if (!orderStatus.find(_.id.toString == OrderCommand.status).isDefined) {
              println(new NoSuchElementException("Status n° "+ OrderCommand.status+" unknow."))
              true
            } else if (databaseOrder.status >= OrderCommand.status) {
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

          case "OrderSetTo" => {
            if (databaseOrder.status == "confirmed") {
              DatabaseService.updateOrderStatus(OrderCommand)
              false
            } else {
              true
              //print("not implemented yet")
            }
          }

          case "OrderSetToPrepared" => {
            if (databaseOrder.status == "confirmed") {
              DatabaseService.updateOrderStatus(OrderCommand)
              false
            } else {
              true
              //print("not implemented yet")
            }
          }

          case "OrderDeleted" => {
            DatabaseService.deleteOrder(OrderCommand.id)
            false
          }

          case _ => {
            println(new NoSuchElementException("Unknow command."))
            true
          }
        }
      }else{
        println("Order n° {} does not exist on database. Action {} aborted.",OrderCommand.id, function)
        true
      }
    }
  }

}
