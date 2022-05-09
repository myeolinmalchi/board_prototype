package controllers

import common.json.CustomJsonApi._
import dto.PostRequestDTO
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import scala.concurrent.ExecutionContext
import services.PostService
import services.PostService._

class PostController @Inject()(cc: ControllerComponents,
                               postService: PostService)
                              (implicit ec: ExecutionContext)
	extends AbstractController(cc) {
	
	private val TITLE_TOO_LARGE_JSON_MSG = (length: Int) =>
		Json.parse(
			s"""
				 |{
				 |  "type": "title",
				 |  "length": $length,
				 |  "max": $TITLE_MAX_LENGTH
				 |}
				 |""".stripMargin)
	
	private val CONTENT_TOO_LARGE_JSON_MSG = (length: Int) =>
		Json.parse(
			s"""
				 |{
				 |  "type": "title",
				 |  "length": $length,
				 |  "max": $TITLE_MAX_LENGTH
				 |}
				 |""".stripMargin)
	
	
	// TODO: Authorization
	def addPost(): Action[AnyContent] = Action.async { implicit request =>
		withJson[PostRequestDTO] { post =>
			postService addPost post map {
				case Right(aff) if aff == post.images.size => Ok
				case Left(ContentTooLarge(length)) =>
					UnprocessableEntity(CONTENT_TOO_LARGE_JSON_MSG(length))
				case Left(TitleTooLarge(length)) =>
					UnprocessableEntity(TITLE_TOO_LARGE_JSON_MSG(length))
				case _ => BadRequest
			} recover {
				case ex: Exception =>
					BadRequest(Json.toJson(Map("msg" -> ex.getMessage)))
			}
		}
	}
	
	def getPost(postId: Int): Action[AnyContent] = Action.async { implicit request =>
		postService getPost postId map {
			case Some(post) => Ok(Json.toJson(post))
			case None => NotFound
		} recover {
			case ex: Exception =>
				BadRequest(Json.toJson(Map("msg" -> ex.getMessage)))
		}
	}
	
	def getPosts(size: Option[Int],
	             page: Option[Int],
	             keyword: Option[String],
	             boardId: Option[Int]): Action[AnyContent] = Action.async { implicit request =>
		postService selectPosts(
			size = size.getOrElse(15),
			page = page.getOrElse(1),
			keyword = keyword,
			boardId = boardId
		) map { post =>
			Ok(Json.toJson(post))
		} recover {
			case ex: Exception =>
				BadRequest(Json.toJson(Map("msg" -> ex.getMessage)))
		}
	}
	
	def getThumbnails(size: Option[Int], boardId: Option[Int]): Action[AnyContent] = Action.async { implicit request =>
		postService selectThumbnails(size.getOrElse(10), boardId) map { thumbnails =>
			Ok(Json.toJson(thumbnails))
		}
	}
	
	def updatePost(postId: Int): Action[AnyContent] = Action.async { implicit request =>
		withJson[PostRequestDTO] { post =>
			postService updatePost post.setPostId(postId) map {
				case Right(_) => Ok
				case Left(ContentTooLarge(length)) =>
					UnprocessableEntity(CONTENT_TOO_LARGE_JSON_MSG(length))
				case Left(TitleTooLarge(length)) =>
					UnprocessableEntity(TITLE_TOO_LARGE_JSON_MSG(length))
				case Left(EmptyPostId) =>
					BadRequest(Json.toJson(Map("msg" -> "postId가 비어있습니다.")))
				case Left(PostNotExist) => NotFound
			} recover {
				case ex: Exception =>
					BadRequest(Json.toJson(Map("msg" -> ex.getMessage)))
			}
		}
	}
}
