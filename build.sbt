name := """server"""

version := "1.0-SNAPSHOT"
scalaVersion := "2.11.7"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

/**
  * We need this, so that SBT can also resolve dependencies from mvn local repository
  */
resolvers += (
  "Local Maven Repository" at "file:///" + Path.userHome.absolutePath + "/.m2/repository"
  )

libraryDependencies ++= Seq(
  javaJpa,
  "org.hibernate" % "hibernate-entitymanager" % "5.1.0.Final",
  evolutions,
  javaWs
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

/**
  * Extension for Encryption
  */
libraryDependencies += "org.mindrot" % "jbcrypt" % "0.3m"

/**
  * For testing
  */
libraryDependencies += "org.easytesting" % "fest-assert" % "1.4"
libraryDependencies += "ch.helin" % "drone-server-messages" % "1.0"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
