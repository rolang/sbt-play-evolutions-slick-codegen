addSbtPlugin("dev.rolang.sbt" % "play-evolutions-slick-codegen" % System.getProperty("plugin.version"))

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "42.2.5"
)
