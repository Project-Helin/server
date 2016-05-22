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
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  javaJpa,
  "org.hibernate" % "hibernate-entitymanager" % "5.1.0.Final",
  "org.hibernate" % "hibernate-java8" % "5.1.0.Final",
  evolutions,
  javaWs
)

/**
  * Add Junit Dependency, since the activator-generated-project
  * didn't have it by default.
  */
libraryDependencies += "junit" % "junit" % "4.12" % "test"
libraryDependencies += "org.mockito" % "mockito-all" % "1.9.5"
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
  * Extension to do graph algorithms in Java
  */
libraryDependencies += "org.jgrapht" % "jgrapht-core" % "0.9.2"


/**
  * Extension for Encryption
  */
libraryDependencies += "org.mindrot" % "jbcrypt" % "0.3m"

libraryDependencies += "commons-beanutils" % "commons-beanutils" % "1.9.2"

libraryDependencies += "com.adrianhurt" %% "play-bootstrap" % "1.1-P25-B3-SNAPSHOT"



/**
  * For testing
  */
libraryDependencies += "org.easytesting" % "fest-assert" % "1.4"
libraryDependencies += "ch.helin" % "commons" % "1.0"

libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "2.51.0"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

/**
  * This is some magic way to let SBT print a summary of
  * tests with their exceptions.
  * The passed arguments are ( as defined from com.novocode.junit.JUnitRunner )
  * -v => verbose
  * -q => quite
  * -a => log assert
  * Don't ask me why -v and -q are enabled, I got these proposal from the
  * #playframework irc chat.
  *
  */
testOptions += Tests.Argument(TestFrameworks.JUnit, "-v", "-q", "-a")

/**
  * We need to use this operation, because otherwise Hibernate can not scan
  * all the entities in production ( only in production, dev mode had no problems ).
  * The reason was that persistence.xml was not present in the classpath.
  * This was the root cause of the bug:
  * https://github.com/playframework/playframework/issues/4590#issuecomment-117051996
  * As far as I understood does this flag keep the persistence.xml in the class path
  * and also all the classes annoated with @Entity
  */
PlayKeys.externalizeResources := false
