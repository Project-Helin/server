name := """server"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

libraryDependencies ++= Seq(
  javaJpa,
  "org.hibernate" % "hibernate-entitymanager" % "5.1.0.Final",
  evolutions
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

/**
  * JDBC Driver for PostgreSQL
  */
libraryDependencies += "org.postgresql" % "postgresql" % "9.3-1104-jdbc41"

/**
  * Extension for Hibernate and PostGIS
  */
libraryDependencies += "org.hibernate" % "hibernate-spatial" % "5.1.0.Final"



// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
