val slickVersion = "3.3.0"
val playVersion = "2.7.2"

lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "play-evolutions-slick-codegen",
    version := "0.0.1",
    organization := "codes.rolang.sbt",
    scalaVersion := "2.12.8",
    libraryDependencies ++= Seq(
      "com.typesafe.slick"  %% "slick"                % slickVersion,
      "com.typesafe.slick"  %% "slick-codegen"        % slickVersion,
      "com.typesafe.play"   %% "play-jdbc-api"        % playVersion,
      "com.typesafe.play"   %% "play-jdbc-evolutions" % playVersion
    ),
    scriptedLaunchOpts := { scriptedLaunchOpts.value ++
      Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false,

    // publish settings
    licenses += ("MIT", url("https://github.com/rolang/sbt-play-evolutions-slick-codegen/blob/master/LICENCE")),
    publishMavenStyle := false,
    bintrayRepository := (if (isSnapshot.value) "sbt-plugins-snapshots" else "sbt-plugins"),
    bintrayOrganization := None,
    bintrayReleaseOnPublish := isSnapshot.value
  )
