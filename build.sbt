name := """server"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

libraryDependencies ++= Seq(
  javaJpa,
  "org.hibernate" % "hibernate-entitymanager" % "4.3.7.Final"
)

/**
  * Add Junit Dependency, since the activator-generated-project
  * didn't have it by default.
  */
libraryDependencies += "junit" % "junit" % "4.12" % "test"

/**
  * RabbitMQ: For connection with the Android-App on the phone
  */
libraryDependencies += "com.rabbitmq" % "amqp-client" % "3.6.0"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
