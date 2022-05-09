package dto

import models.Tables.AdminRow
import play.api.libs.json.Json

case class AdminDTO(id: String,
                    pw: String,
                    name: Option[String],
                    email: Option[String],
                    phone: Option[String])

object AdminDTO {
	implicit val adminWrites = Json.writes[AdminDTO]
	implicit val adminReads = Json.reads[AdminDTO]
	implicit val rowToDto = (row: AdminRow) =>
		AdminDTO(
			id = row.id,
			pw = row.password,
			name = row.name,
			email = row.email,
			phone = row.phone
		)
}
