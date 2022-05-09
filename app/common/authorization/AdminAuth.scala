package common.authorization

import java.util.concurrent.TimeUnit
import pdi.jwt.{JwtAlgorithm, JwtClaim, JwtJson}
import play.api.mvc.Result

object AdminAuth {
	val key = "testKey"
	val algo = JwtAlgorithm.HS256
	
	val ROLE_ADMIN = "admin"
	implicit class CustomResult(result: Result) {
		def withJwtHeader(id: String, role: String): Result = {
			val claims = JwtClaim (
				expiration = Some(System.currentTimeMillis() / 1000 + TimeUnit.DAYS.toSeconds(1)),
				issuedAt = Some(System.currentTimeMillis() / 1000),
				content =
					s""" {
						 | 	"id": "$id",
						 |	"role": "$role"
						 | }""".stripMargin
			)
			val token = JwtJson.encode(claims, key, algo)
			result.withHeaders("Authorization"-> token)
		}
	}
}
