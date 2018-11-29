name := "clientUnigate"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies := Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.14",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.14",
  "com.typesafe.akka" %% "akka-stream" % "2.5.14",
  "com.typesafe.akka" %% "akka-http-core" % "10.1.3",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.3",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.14",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)