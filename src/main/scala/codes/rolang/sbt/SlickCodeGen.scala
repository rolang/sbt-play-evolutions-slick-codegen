package codes.rolang.sbt

import sbt._
import java.io.{File, FileInputStream, InputStream}
import java.sql.{Connection, SQLException}
import javax.sql.DataSource
import play.api.db.TransactionIsolationLevel
import play.api.db.evolutions.{Evolutions, ResourceEvolutionsReader}
import sbt.internal.util.ManagedLogger
import scala.concurrent.ExecutionContext.Implicits.global
import slick.codegen.SourceCodeGenerator
import slick.jdbc.{DataSourceJdbcDataSource, JdbcProfile}
import slick.model.Model
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class SlickCodeGen(outputDir: File,
                   outputPackage: String,
                   profile: JdbcProfile,
                   profileSrc: Option[String],
                   jdbcDriver: String,
                   dbUrl: String,
                   excludeTables: Seq[String],
                   evolutionsName: String,
                   generator: Model => SourceCodeGenerator,
                   logger: ManagedLogger) {

  import profile.api._

  def apply(forceRegeneration: Boolean = false): Seq[File] = {
    if (forceRegeneration || !outputFiles.forall(_.exists()) || outputFilesOutdated) {
      logger.info(s"Generating slick models")

      val files = generateProfileFile.foldLeft(Seq(generateDbModels))(_ :+ _)

      logger.info(s"Slick models created in:\n${files.map(_.getAbsolutePath).mkString("\n")}")

      files
    } else {
      logger.info(s"Regenerating slick models is not required")
      outputFiles
    }
  }

  def outputFilesOutdated: Boolean = {
    isProfileFileOutdated || lastModified(tablesOutputFile) < lastModified(new File(Evolutions.directoryName(evolutionsName)))
  }

  private def isProfileFileOutdated: Boolean = {
    (for {
      profileSrc <- profileSrcFile
      profileOut <- profileOutputFile
    } yield lastModified(profileOut) < lastModified(profileSrc)).getOrElse(false)
  }

  private def lastModified(file: File): Long = {
    FileInfo.lastModified(file).lastModified
  }

  def outputFiles: Seq[File] = {
    profileOutputFile.foldLeft(Seq(tablesOutputFile))(_ :+ _)
  }

  private val pkgPath = outputPackage.replace('.', Path.sep)

  private val profileSrcFile: Option[File] = profileSrc.map(path => new File(path))

  val tablesOutputFile: File = outputDir / pkgPath / "Tables.scala"

  val profileOutputFile: Option[File] = profileSrcFile.map(f => outputDir / pkgPath / f.getName)

  private def generateDbModels: File = {
    val tables = profile.defaultTables.map(_.filterNot(t => excludeTables.contains(t.name.name)))
    val db = Database.forURL(url = dbUrl, driver = jdbcDriver)
    val playDb = new PlayDatabase(db)

    def cleanupDb(): Unit = {
      Evolutions.cleanupEvolutions(playDb)
    }

    cleanupDb()

    val file = Evolutions.withEvolutions(playDb, EvolutionsReader) {
      Await.result(
        db.run {
          profile.createModel(Some(tables))
        }.map { model =>
          generator(model).writeToFile(
            profile.getClass.getName.stripSuffix("$"),
            outputDir.getAbsolutePath,
            outputPackage,
            tablesOutputFile.getName.takeWhile(_ != '.'),
            tablesOutputFile.getName
          )

          tablesOutputFile
        },
        Duration.Inf
      )
    }

    cleanupDb()
    file
  }

  private def generateProfileFile: Option[File] = {
    for {
      src <- profileSrcFile
      out <- profileOutputFile
    } yield {
      IO.write(out, s"package $outputPackage\n" + IO.read(src))
      out
    }
  }

  private object EvolutionsReader extends ResourceEvolutionsReader {
    def loadResource(db: String, revision: Int): Option[InputStream] = {
      val sqlFile = new File(Evolutions.fileName(db, revision))
      if (sqlFile.exists()) Some(new FileInputStream(sqlFile)) else None
    }
  }

  // from private play.api.db.slick.evolutions.internal.DBApiAdapter.DatabaseAdapter
  // https://github.com/playframework/play-slick/blob/master/src/evolutions/src/main/scala/play/api/db/slick/evolutions/internal/DBApiAdapter.scala
  private class PlayDatabase(db: Database) extends play.api.db.Database {
    def name: String = evolutionsName

    def dataSource: DataSource = {
      db.source.asInstanceOf[DataSourceJdbcDataSource].ds
    }

    val url: String = withConnection(_.getMetaData.getURL())

    def getConnection(): Connection = db.source.createConnection()

    def getConnection(autocommit: Boolean): Connection = {
      val conn = getConnection()
      conn.setAutoCommit(autocommit)
      conn
    }

    def withConnection[A](block: Connection => A): A = {
      val conn = getConnection()
      try block(conn)
      finally {
        try conn.close() catch {
          case _: SQLException =>
        }
      }
    }

    def withConnection[A](autocommit: Boolean)(block: Connection => A): A = withConnection { conn =>
      conn.setAutoCommit(autocommit)
      block(conn)
    }

    def withTransaction[A](block: Connection => A): A = {
      val conn = getConnection()
      var done = false
      try {
        val res = block(conn)
        conn.commit()
        done = true
        res
      } finally {
        if (!done) conn.rollback()
        conn.close()
      }
    }

    def withTransaction[A](l: TransactionIsolationLevel)(block: Connection => A): A = {
      withTransaction(block)
    }

    def shutdown(): Unit = ()
  }

}
