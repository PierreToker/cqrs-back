name := "order-back-cqrs"
version := "0.1"
scalaVersion := "2.12.8"

lazy val mongoVersion    = "2.6.0"
lazy val kafkaVersion    = "2.2.2"
lazy val sprayJSONVersion    = "1.3.5"

libraryDependencies ++= Seq(
  "org.scalatest"     %% "scalatest"            % "3.0.5"         % Test,
  "org.mongodb.scala" %% "mongo-scala-driver"   % mongoVersion,
  "org.apache.kafka"  %% "kafka"                % kafkaVersion,
  //"io.spray" %% "spray-json" % sprayJSONVersion
  "net.liftweb" %% "lift-json" % "3.4.1"
)