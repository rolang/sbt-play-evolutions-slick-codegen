val slickVersion = "3.3.0"
val playVersion = "2.7.2"

lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "play-evolutions-slick-codegen",
    version := "0.1.0",
    organization := "dev.rolang.sbt",
    homepage := Some(url("https://github.com/rolang/sbt-play-evolutions-slick-codegen")),
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
    publishMavenStyle := true,
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/rolang/sbt-play-evolutions-slick-codegen"),
        "scm:git@github.com:rolang/sbt-play-evolutions-slick-codegen.git"
      )
    ),
    developers := List(
      Developer(
        id = "rolang",
        name = "Roman Langolf",
        email = "rolang@pm.me",
        url = url("https://rolang.dev")
      )
    ),
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
      else Some("releases" at nexus + "service/local/staging/deploy/maven2")
    }
  )
