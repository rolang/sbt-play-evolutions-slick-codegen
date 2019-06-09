# sbt-play-evolutions-slick-codegen

Slick code generator sbt plugin for play evolutions.

Add plugin to `plugins.sbt` in your project.

```
addSbtPlugin("codes.rolang.sbt" % "play-evolutions-slick-codegen" % "0.1.0")
```

Enable in your project
```scala
lazy val myProject = (project in file(".")).enablePlugins(PlayEvolutionsSlickCodeGenPlugin)
```

```bash
# ensure the database service is running (if you don't use an in-memory database), e.g.
docker run -e POSTGRES_PASSWORD=passwd -p 5432:5432 -d postgres
```

Settings:
```scala
// required
slickCodeGenDbUrl := "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=passwd",

// optional := default value
slickCodeGenJdbcDriver      := "org.postgresql.Driver",
slickCodeGenPlayEvolutions  := "default",
slickCodeGenOutputPackage   := "com.example",
slickCodeGenOutputDir       := (sourceManaged in Compile).value,
slickCodeGenExcludedTables  := Seq("play_evolutions"),
slickCodeGenProfile         := slick.jdbc.PostgresProfile,

// path to a custom profile file, e.g. Some("project/CustomPostgresProfile.scala")
// will be added to generated sources
slickCodeGenProfileSrc      := None,

// customized SourceCodeGenerator
slickCodeGenCodeGenerator   := {
    m: slick.model.Model => new slick.codegen.SourceCodeGenerator(m)
}
```

```bash
# generator will run on changes to play evolution scripts
# watch and re-compile on changes by e.g.
sbt ~compile

# to force code re-generation, execute the task
sbt slickCodeGen
```
