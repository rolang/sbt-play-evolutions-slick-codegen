crossScalaVersions := Seq("2.12.13", "2.13.5")

lazy val testRoot = (project in file("."))
  .enablePlugins(PlayEvolutionsSlickCodeGenPlugin)
  .settings(
    name := "test",
    version := "0.1",
    organization := "test",
    slickCodeGenDbUrl := "jdbc:postgresql://localhost:5432/postgres?user=postgres"
  )
