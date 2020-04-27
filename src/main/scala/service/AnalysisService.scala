package service

import entity.Order

object AnalysisService {
  def analysis(OrderCommand: Order, function: String) = {
    if (function.equals("OrderCreated")){
      DatabaseService.createOrder(OrderCommand)
    }else{
      val databaseShipping = DatabaseService.getTransferById(OrderCommand.id)
      if (databaseShipping != None) {
        function match {
          case "OrderSetToPrepared" => {
            if (databaseShipping.status == "confirmed") {
              DatabaseService.updateOrderStatus(OrderCommand)
            } else {
              print("not implemented yet")
            }
          }
          case "OrderDeleted" => {
            DatabaseService.deleteOrder(OrderCommand.id)
          }
          case _ => println("Unknow request")
        }
      }else{
        println("Order {} does not exist on database. Action {} aborted.",OrderCommand.id, function)
      }
    }
  }
}
