package service

import java.time.LocalDateTime

import entity.{Event, Order}
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import util.Helpers._

object DatabaseEventService {

  val mongoClient: MongoClient = MongoClient()
  val codecRegistry: CodecRegistry = fromRegistries(fromProviders(classOf[Event],classOf[Order]), DEFAULT_CODEC_REGISTRY)
  val database: MongoDatabase = mongoClient.getDatabase("order-eventStore").withCodecRegistry(codecRegistry)
  val collection: MongoCollection[Event] = database.getCollection("events")

  /**
   * Insert single event
   * @param function Action made
   * @param order Order concerned by the action
   */
  def insertEvent(function : String,order: Order): Event = {
    val event = Event(getFreeEventId.getOrElse(0),LocalDateTime.now().toString, function,true, order)
    collection.insertOne(event).results()
    event
  }

  /**
   * Update error field of an event
   * @param eventId Which event
   * @param errorStatus If the event process has generated an error during his execution
   */
  def updateEventError(eventId: Int, errorStatus: Boolean): Unit = {
    collection.findOneAndUpdate(equal("id", eventId), set("errorDuringExecution", errorStatus)).results()
  }

  /**
   * Get last event inserted on the store
   * @return Event found
   */
  def getLastEventInserted : Event = {
    collection.find().sort(equal("_id",-1)).limit(1).headResult()
  }

  /**
   * Get last event on a specific order
   * @param orderId Which order
   * @return Event found
   */
  def getLastEventInsertedOnOrder(orderId: Int) : Event = {
    collection.find(equal("order.id",orderId)).sort(equal("_id",-1)).limit(1).headResult()
  }

  /**
   * Get all events relative to a specific order
   * @param orderId Which order
   * @return Events grouped in a sequence
   */
  def getEventsOrder(orderId : Int) : Seq[Event] = {
    collection.find(and(equal("order.id",orderId),equal("errorDuringExecution",false))).results()
  }

  /**
   * Get a free id
   * @return Id found or zero if store is empty
   */
  def getFreeId : Option[Int] = {
    try {
      Some(collection.find().sort(equal("order.id",-1)).limit(1).headResult().order.id.getOrElse(0) + 1)
    } catch {
      case e: Exception => Some(0)
    }
  }

  /**
   * Get a free id for an event
   * @return Id found or zero if store is empty
   */
  def getFreeEventId : Option[Int] = {
    try {
      Some(collection.find().sort(equal("id",-1)).limit(1).headResult().id + 1)
    } catch {
      case e: Exception => Some(0)
    }
  }

}
