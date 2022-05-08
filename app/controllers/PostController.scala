package controllers

import javax.inject.Inject
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import scala.concurrent.ExecutionContext
import services.PostService
import common.json.CustomJsonApi._
import dto.PostRequestDTO
import play.api.libs.json.Json
import services.PostService._

class PostController @Inject() (cc: ControllerComponents,
								postService: PostService)
							   (implicit ec: ExecutionContext)
		extends AbstractController(cc){
	
	// TODO: Authorization
	def addPost(): Action[AnyContent] = Action.async { implicit request =>
		withJson[PostRequestDTO] { post =>
			postService addPost post map {
				case Right(aff) if aff == post.images.size => Created
				case Left(ContentLengthTooLarge(length)) =>
					UnprocessableEntity(Json.toJson(Map(
						"type" -> "content",
						"length" -> length.toString,
						"max" -> CONTENT_MAX_LENGTH.toString
					)))
				case Left(TitleLengthTooLarge(length)) =>
					UnprocessableEntity(Json.toJson(Map(
						"type" -> "title",
						"length" -> length.toString,
						"max" -> TITLE_MAX_LENGTH.toString
					)))
				case _ => BadRequest
			} recover {
				case ex: Exception =>
					BadRequest(Json.toJson("msg" -> ex.getMessage))
			}
		}
	}
	
	def getPost(postId: Int): Action[AnyContent] = Action.async { implicit request =>
		postService getPost postId map {
			case Some(post) => Ok(Json.toJson(post))
			case None => NotFound
		} recover {
			case ex: Exception =>
				BadRequest(Json.toJson("msg" -> ex.getMessage))
		}
	}
	
	def getPosts(size: Option[Int],
				 page: Option[Int],
				 keyword: Option[String],
				 boardId: Option[Int]):Action[AnyContent] = Action.async { implicit request =>
		postService selectPosts (
			size = size.getOrElse(30),
			page = page.getOrElse(1),
			keyword = keyword,
			boardId = boardId
		) map { post =>
			Ok(Json.toJson(post))
		} recover {
			case ex: Exception =>
				BadRequest(Json.toJson("msg" -> ex.getMessage))
		}
	}
	
	def getThumbnails(size: Option[Int], boardId: Option[Int]): Action[AnyContent] = Action.async { implicit request =>
		postService selectThumbnails (size.getOrElse(10), boardId) map { thumbnails =>
			Ok(Json.toJson(thumbnails))
		}
	}
	
}
