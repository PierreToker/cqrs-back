import java.util
import java.util.Properties

import entity.{Order, OrderStatus}
import net.liftweb.json._
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import service.{AnalysisService, DatabaseEventService, DatabaseService, SynchronizeService}

import scala.collection.JavaConverters._

object Server extends App {
  implicit val formats = DefaultFormats

  val props = new Properties()
  props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
  props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
  props.put(ConsumerConfig.AUTO_OFFSET_RESET_DOC, "latest")
  props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group")

  val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](props)
  consumer.subscribe(util.Arrays.asList("orders"))

  var orderStatus: Seq[OrderStatus] = DatabaseService.getOrderStatus()

  println(s"Listener up on: " + props.getProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG))
  while (true) {
    val record = consumer.poll(1000).asScala
    for (data <- record.iterator) {
      println(s"Key:" + data.key())
      println(data.value())
      val jValue = parse(data.value())
      val order = jValue.extract[Order]
      var error: Boolean = false
      if (data.key() == "OrderRestored") {
        error = SynchronizeService.replayOrder(order,data.key())
      }else{
        error = AnalysisService.analysis(order, data.key())
        DatabaseEventService.insertEvent(data.key(), error, order)
      }
      if (error)
        println("Error during process")
    }
  }
}