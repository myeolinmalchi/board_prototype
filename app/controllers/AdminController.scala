package controllers

import common.authorization.AdminAuth.{CustomResult, ROLE_ADMIN}
import common.json.CustomJsonApi.withJson
import common.validation.ValidationResultLib
import dto.AdminDTO
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import scala.concurrent.{ExecutionContext, Future}
import services.{AccountService, AuthService}

class AdminController @Inject() (cc: ControllerComponents)
                                (implicit ec: ExecutionContext,
                                 accountService: AccountService,
                                 authService: AuthService)
	extends AbstractController(cc) with ValidationResultLib[Future]{
	
	import authService.withAuth
	
	def login: Action[AnyContent] = Action.async { implicit request =>
		withJson[AdminDTO] { implicit admin =>
			accountService.login map {
				case true => Ok.withJwtHeader(admin.id, ROLE_ADMIN)
				case false => Unauthorized
			}	getOrElse NotFound
		}
	}
	
	def regist: Action[AnyContent] = Action.async { implicit request =>
		withAuth {
			withJson[AdminDTO] { implicit admin =>
				accountService.regist map {
					case Right(_) => Created
					case Left(accountService.ValidationFailure(msg))	=>
						UnprocessableEntity(Json.obj("msg" -> msg))
				}
			}
		}
	}
	
}
