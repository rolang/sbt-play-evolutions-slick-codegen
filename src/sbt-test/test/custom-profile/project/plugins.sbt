addSbtPlugin("dev.rolang.sbt" % "play-evolutions-slick-codegen" % System.getProperty("plugin.version"))

val slickPgVersion = "0.19.5"

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "42.2.19",
  "com.github.tminglei" %% "slick-pg" % slickPgVersion,
  "com.github.tminglei" %% "slick-pg_play-json" % slickPgVersion
)
