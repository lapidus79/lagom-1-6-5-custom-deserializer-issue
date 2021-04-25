organization in ThisBuild := "com.blah"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.13.0"

lazy val `jacksontest` = (project in file("."))
  .aggregate(`jacksontest-api`, `jacksontest-impl`)

lazy val `jacksontest-api` = (project in file("jacksontest-api"))
  .settings(common)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslApi,
      lombok
    )
  )

lazy val `jacksontest-impl` = (project in file("jacksontest-impl"))
  .enablePlugins(LagomJava)
  .settings(common)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslPersistenceCassandra,
      lagomJavadslKafkaBroker,
      lagomLogback,
      lagomJavadslTestKit,
      lombok,
      /*
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.11.4" force(),
      "com.fasterxml.jackson.core" % "jackson-core" % "2.11.4" force(),
      "com.fasterxml.jackson.core" % "jackson-annotations" % "2.11.4" force(),
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.11.4" force(),
      "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.11.4" force(),
      "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.11.4" force(),
      "com.fasterxml.jackson.datatype" % "jackson-datatype-guava" % "2.11.4" force(),
      "com.fasterxml.jackson.datatype" % "jackson-datatype-pcollections" % "2.11.4" force(),
      //"com.typesafe.akka" %% "akka-serialization-jackson" % "2.6.14" force(),
      //"com.typesafe.play" %% "play-json-jackson" % "2.9.1" force(),
      ("com.fasterxml.jackson.module" % "jackson-module-paranamer" % "2.11.4"),
      ("com.fasterxml.jackson.module" % "jackson-module-parameter-names" % "2.11.4")
       */
    ),
    /*
    dependencyOverrides ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.11.4" force(),
      "com.fasterxml.jackson.core" % "jackson-core" % "2.11.4" force(),
      "com.fasterxml.jackson.core" % "jackson-annotations" % "2.11.4" force(),
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.11.4" force(),
      "com.fasterxml.jackson.module" % "jackson-module-paranamer" % "2.11.4"  force(),
      "com.fasterxml.jackson.module" % "jackson-module-parameter-names" % "2.11.4"  force(),
      "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.11.4" force(),
      "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.11.4" force(),
      "com.fasterxml.jackson.datatype" % "jackson-datatype-guava" % "2.11.4" force(),
      "com.fasterxml.jackson.datatype" % "jackson-datatype-pcollections" % "2.11.4" force(),
      //"com.typesafe.akka" %% "akka-serialization-jackson" % "2.6.3" force(),
      //"com.typesafe.play" %% "play-json-jackson" % "2.8.1" force(),
    )
     */
  )
  .settings(lagomForkedTestSettings)
  .dependsOn(`jacksontest-api`)

val lombok = "org.projectlombok" % "lombok" % "1.18.20"

def common = Seq(
  javacOptions in Compile += "-parameters"
)
