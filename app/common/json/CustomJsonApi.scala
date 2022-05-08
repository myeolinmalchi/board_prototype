package common.json

import play.api.libs.json.{JsError, JsSuccess, JsValue, Json, Reads}
import play.api.mvc.Results.{BadRequest, Ok, Redirect}
import play.api.mvc.{AnyContent, Call, Request, Result}
import scala.concurrent.{ExecutionContext, Future}

object CustomJsonApi {
	
	def withJson[A](f: A => Future[Result])
				   (implicit request: Request[AnyContent], reads: Reads[A]): Future[Result] = {
		request.body.asJson.map { body =>
			Json.fromJson[A](body) match {
				case JsSuccess(a, path) => f(a)
				case e @JsError(_) =>
					Future.successful(BadRequest(e.toString))
			}
		}.getOrElse(Future.successful(BadRequest))
	}
	
	def withAnyJson(f: JsValue => Future[Result])
				   (implicit request: Request[AnyContent], ec: ExecutionContext): Future[Result] = {
		request.body.asJson match {
			case Some(value) => f(value)
			case None => Future.successful(BadRequest)
		}
	}
}