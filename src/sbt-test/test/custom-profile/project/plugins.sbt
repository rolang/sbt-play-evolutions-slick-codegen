addSbtPlugin("codes.rolang.sbt" % "play-evolutions-slick-codegen" % System.getProperty("plugin.version"))

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "42.2.5",
  "com.github.tminglei" %% "slick-pg" % "0.17.2",
  "com.github.tminglei" %% "slick-pg_play-json" % "0.17.2"
)
