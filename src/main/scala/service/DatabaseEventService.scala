package service

import java.time.LocalDateTime

import entity.{Event, Order}
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.model.Filters._
import util.Helpers._

object DatabaseEventService {

  val mongoClient: MongoClient = MongoClient()
  val codecRegistry: CodecRegistry = fromRegistries(fromProviders(classOf[Event],classOf[Order]), DEFAULT_CODEC_REGISTRY)
  val database: MongoDatabase = mongoClient.getDatabase("order-eventStore").withCodecRegistry(codecRegistry)
  val collection: MongoCollection[Event] = database.getCollection("events")

  //Return one transfer with this id. Inside function app only
  def getEvent(id: Integer): Event = {
    collection.find(equal("idEvent", id)).headResult()
  }

  /**
   * Insert single event
   * @param function
   * @param shipping
   */
  def insertEvent(function : String,errorDuringExecution: Boolean ,shipping: Order): Unit = {
    val event = Event(LocalDateTime.now().toString, function,errorDuringExecution, shipping)
    collection.insertOne(event).results()
  }

  def getLastInsertd() : Event = {
    collection.find().sort(equal("_id",-1)).limit(1).headResult()
  }

  /**
   * Get all events relative to a specific order
   * @param orderId Which order
   * @return Events grouped in a sequence
   */
  def getEventsOrder(orderId : Int) : Seq[Event] = {
    collection.find(and(equal("order.id",orderId),equal("errorDuringExecution",false))).results()
  }
}
