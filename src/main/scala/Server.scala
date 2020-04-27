
import java.util
import java.util.Properties

import entity.Order
import net.liftweb.json._
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import service.{AnalysisService, DatabaseEventService, DatabaseService}

import scala.collection.JavaConverters._

object Server extends App {
  //KafkaConsumerService.runConsumer()

  implicit val formats = DefaultFormats

  val props = new Properties()
  props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
  props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
  props.put(ConsumerConfig.AUTO_OFFSET_RESET_DOC, "latest")
  props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group")

  val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](props)
  consumer.subscribe(util.Arrays.asList("orders"))

  while (true) {
    val record = consumer.poll(1000).asScala
    for (data <- record.iterator) {
      println(s"Key:" + data.key())
      println(data.value())
      val jValue = parse(data.value())
      val shipping = jValue.extract[Order]
      DatabaseEventService.insertEvent(data.key(),shipping)
      AnalysisService.analysis(shipping,data.key())

      /*
      println("New entry")
      println(data.key())
      println(data.value())
      data.key() match {
        case "new" => {
          val jValue = parse(data.value())
          val shipping = jValue.extract[Shipping]
          DatabaseService.addTransfer(shipping)
        }
        case "update" => {
          val jValue = parse(data.value())
          val shipping = jValue.extract[Shipping]
          DatabaseService.updateTransfer(shipping.id, shipping.status)
        }
        case "delete" => {
          DatabaseService.deleteTransfer(data.value().toInt)
        }
        case _ => println("Unknow request")
      }
      */
    }
  }
}