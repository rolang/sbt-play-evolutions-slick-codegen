package codes.rolang.sbt

import sbt._
import sbt.Keys._
import java.io.File
import slick.codegen.SourceCodeGenerator
import slick.jdbc.JdbcProfile
import slick.model.Model

object PlayEvolutionsSlickCodeGenPlugin extends AutoPlugin {

  object autoImport {
    lazy val slickCodeGen = taskKey[Seq[File]]("Generate slick models")

    lazy val slickCodeGenDbUrl: SettingKey[String] =
      settingKey[String]("URL of database used by codegen")

    lazy val slickCodeGenPlayEvolutions: SettingKey[String] =
      settingKey[String]("Name of evolutions directory")

    lazy val slickCodeGenProfile: SettingKey[JdbcProfile] =
      settingKey[JdbcProfile]("Jdbc profile used by codegen")

    lazy val slickCodeGenProfileSrc: SettingKey[Option[String]] =
      settingKey[Option[String]]("Path to a custom profile source file which will be added to generated source")

    lazy val slickCodeGenJdbcDriver: SettingKey[String] =
      settingKey[String]("Jdbc driver used by codegen")

    lazy val slickCodeGenOutputPackage: SettingKey[String] =
      settingKey[String]("Package of generated code")

    lazy val slickCodeGenOutputDir: SettingKey[File] =
      settingKey[File]("Output directory of generated code")

    lazy val slickCodeGenCodeGenerator: SettingKey[Model => SourceCodeGenerator] =
      settingKey[Model => SourceCodeGenerator]("Function that provides the SourceCodeGenerator")

    lazy val slickCodeGenExcludedTables: SettingKey[Seq[String]] =
      settingKey[Seq[String]]("Tables that should be excluded")
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    slickCodeGenProfile := slick.jdbc.PostgresProfile,
    slickCodeGenProfileSrc := None,
    slickCodeGenJdbcDriver := "org.postgresql.Driver",
    slickCodeGenDbUrl := "Database url is not set",
    slickCodeGenPlayEvolutions := "default",
    slickCodeGenOutputPackage := "com.example",
    slickCodeGenOutputDir := (sourceManaged in Compile).value,
    slickCodeGenExcludedTables := Seq("play_evolutions"),
    slickCodeGenCodeGenerator := { m: Model => new SourceCodeGenerator(m) },

    slickCodeGen := {
      new SlickCodeGen(
        outputDir = (sourceManaged in Compile).value,
        outputPackage = slickCodeGenOutputPackage.value,
        evolutionsName = slickCodeGenPlayEvolutions.value,
        profile = slickCodeGenProfile.value,
        profileSrc = slickCodeGenProfileSrc.value,
        jdbcDriver = slickCodeGenJdbcDriver.value,
        dbUrl = slickCodeGenDbUrl.value,
        excludeTables = slickCodeGenExcludedTables.value,
        generator = slickCodeGenCodeGenerator.value,
        logger = streams.value.log)(true)
    },

    sourceGenerators in Compile += Def.task {
      new SlickCodeGen(
        outputDir = (sourceManaged in Compile).value,
        outputPackage = slickCodeGenOutputPackage.value,
        evolutionsName = slickCodeGenPlayEvolutions.value,
        profile = slickCodeGenProfile.value,
        profileSrc = slickCodeGenProfileSrc.value,
        jdbcDriver = slickCodeGenJdbcDriver.value,
        dbUrl = slickCodeGenDbUrl.value,
        excludeTables = slickCodeGenExcludedTables.value,
        generator = slickCodeGenCodeGenerator.value,
        logger = streams.value.log)()
    }
  )
}
