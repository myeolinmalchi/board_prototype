package services

import cats.data.OptionT
import common.authorization.AdminAuth
import common.validation.ValidationResultLib
import javax.inject.Inject
import models.AdminModel
import pdi.jwt.{JwtClaim, JwtJson}
import play.api.mvc.{AnyContent, Request, Result}
import scala.concurrent.{ExecutionContext, Future}
import services.AuthService._
import common.authorization.AdminAuth._
import dto.AdminDTO
import play.api.libs.json.Json
import play.api.mvc.Results.{Forbidden, NotFound, Unauthorized}
import scala.util.{Failure, Success, Try}

class AuthServiceImpl @Inject() () (implicit ec: ExecutionContext, adminModel: AdminModel)
	extends AuthService with ValidationResultLib[Future]{
	
	case class AdminAuth()(implicit request: Request[AnyContent]) {
		private def checkTokenValid(token: String): ValidationResult[AuthFailure, Unit] =
			ValidationResult.ensure(
				JwtJson.isValid(token, key, Seq(algo)),
				onFailure = InvalidToken
			)
			
		private def checkTokenNotExpired(claims: JwtClaim): ValidationResult[AuthFailure, Unit] =
			ValidationResult.ensure(
				claims.expiration.getOrElse(0L) > System.currentTimeMillis() / 1000,
				onFailure = ExpiredToken
			)
			
		private def checkRole(role :String): ValidationResult[AuthFailure, Unit] =
			ValidationResult.ensure(role.equals(ROLE_ADMIN), onFailure = IncorrectAuth)
			
		private def checkAccountExist(id: String): ValidationResult[AuthFailure, Unit] =
			ValidationResult.ensureM(
				adminModel.checkAccountExists(id),
				onFailure = NotExistID
			)
			
		private def getJsonContent(claims: JwtClaim): (String, String) = {
			val json = Json.parse(claims.content)
			val role = (json \ "role").as[String]
			val id = (json \ "id").as[String]
			(role, id)
		}
		
		def validation: Future[Either[AuthFailure, Unit]] = {
			(for {
				token <- OptionT.fromOption[Try](request.headers.get("Authorization"))
				claims <- OptionT.liftF(JwtJson.decode(token, key, Seq(algo)))
				(role, id) = getJsonContent(claims)
			} yield for {
				_ <- checkTokenValid(token)
				_ <- checkTokenNotExpired(claims)
				_ <- checkRole(role)
				_ <- checkAccountExist(id)
			} yield ()).getOrElse(ValidationResult.failed[AuthFailure, Unit](NoToken)) match {
				case Success(rs: ValidationResult[AuthFailure, Unit]) => rs
				case Failure(_) => ValidationResult.failed(InvalidToken)
			}
		}.value
		
		def auth(authSuccess: => Future[Result]): Future[Result] =
			validation flatMap {
				case Right(_) => authSuccess
				case Left(InvalidToken) => Future(Unauthorized)
				case Left(ExpiredToken) => Future(Unauthorized)
				case Left(IncorrectAuth) => Future(Forbidden)
				case Left(NotExistID) => Future(NotFound)
				case Left(NoToken) => Future(Unauthorized)
			}
	}
	
	override def withAuth(f: => Future[Result])(implicit request: Request[AnyContent]): Future[Result] =
		AdminAuth().auth(f)
	
}
