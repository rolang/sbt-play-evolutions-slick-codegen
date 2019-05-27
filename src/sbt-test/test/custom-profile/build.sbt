lazy val testRoot = (project in file("."))
  .enablePlugins(PlayEvolutionsSlickCodeGenPlugin)
  .settings(
    name := "test",
    version := "0.1",
    organization := "test",
    scalaVersion := "2.12.8",
    slickCodeGenDbUrl := "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=passwd",
    slickCodeGenProfile := CustomPostgresProfile,
    slickCodeGenProfileSrc := Some("project/CustomPostgresProfile.scala")
  )
