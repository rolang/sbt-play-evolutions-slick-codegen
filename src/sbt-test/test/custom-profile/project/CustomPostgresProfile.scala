import com.github.tminglei.slickpg._
import com.github.tminglei.slickpg.str.PgStringSupport
import slick.basic.Capability
import slick.jdbc.JdbcCapabilities

trait CustomPostgresProfile extends ExPostgresProfile
    with PgPlayJsonSupport
    with PgSearchSupport
    with PgStringSupport
    with PgArraySupport {

  def pgjson: String = "jsonb"

  override val api: CustomAPI.type = CustomAPI

  object CustomAPI extends API
    with JsonImplicits
    with SearchImplicits
    with SearchAssistants
    with PgStringImplicits
    with ArrayImplicits
}

object CustomPostgresProfile extends CustomPostgresProfile
