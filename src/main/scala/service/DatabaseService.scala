package service

import entity.{Event, Order, OrderStatus}
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import util.Helpers._

object DatabaseService {

  val mongoClient: MongoClient = MongoClient()
  val codecRegistry: CodecRegistry = fromRegistries(fromProviders(classOf[Order]), DEFAULT_CODEC_REGISTRY)
  val database: MongoDatabase = mongoClient.getDatabase("orders-db").withCodecRegistry(codecRegistry)
  val collection: MongoCollection[Order] = database.getCollection("orders")

  //Return one transfer with this id. Inside function app only
  def getTransferById(id: Integer): Order = {
    collection.find(equal("id", id)).headResult()
  }

  /**
   * Create single order
   * @param order
   */
  def createOrder(order: Order): Unit = {
    collection.insertOne(order).results()
  }

  /**
   * Update status of an order
   * @param commandOnShipping
   */
  def updateOrderStatus(commandOnShipping: Order): Unit = {
    collection.updateOne(equal("id", commandOnShipping.id), set("status", commandOnShipping.status)).results()
  }

  /**
   * Delete single order
   * @param id
   */
  def deleteOrder(id: Int): Unit = {
    collection.deleteOne(equal("id", id)).results()
  }

  /**
   * Get every order status
   */
  def getOrderStatus(): Seq[OrderStatus] = {
    val codecRegistry: CodecRegistry = fromRegistries(fromProviders(classOf[OrderStatus]), DEFAULT_CODEC_REGISTRY)
    val database: MongoDatabase = mongoClient.getDatabase("orders-db").withCodecRegistry(codecRegistry)
    val table: MongoCollection[OrderStatus] = database.getCollection("orderStatus")
    table.find.results()
  }
}
