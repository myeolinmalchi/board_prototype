package controllers

import common.json.CustomJsonApi._
import dto.PostRequestDTO
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import scala.concurrent.{ExecutionContext, Future}
import services.{AuthService, PostService}
import services.PostService._

class PostController @Inject()(cc: ControllerComponents,
                               postService: PostService,
                               authService: AuthService)
                              (implicit ec: ExecutionContext)
	extends AbstractController(cc) {
	
	import authService.withAuth
	
	private val TITLE_TOO_LARGE_JSON_MSG = (length: Int) =>
		Json.obj(
			"type" -> "title",
			"length" -> length,
			"max" -> TITLE_MAX_LENGTH
		)
	
	private val CONTENT_TOO_LARGE_JSON_MSG = (length: Int) =>
		Json.obj(
			"type" -> "content",
			"length" -> length,
			"max" -> CONTENT_MAX_LENGTH
		)
	
	def addPost(): Action[AnyContent] = Action.async { implicit request =>
		withAuth {
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
						BadRequest(Json.obj("msg" -> ex.getMessage))
				}
			}
		}
	}
	
	def getPost(postId: Int): Action[AnyContent] = Action.async { implicit request =>
		postService getPost postId map {
			case Some(post) => Ok(Json.toJson(post))
			case None => NotFound
		} recover {
			case ex: Exception =>
				BadRequest(Json.obj("msg" -> ex.getMessage))
		}
	}
	
	def getPosts(size: Option[Int],
	             page: Option[Int],
	             keyword: Option[String],
	             boardId: Option[Int]): Action[AnyContent] = Action.async { implicit request =>
		withAuth(
			authSuccess =
				postService selectPosts(
					 size = size.getOrElse(15),
					 page = page.getOrElse(1),
					 keyword = keyword,
					 boardId = boardId
				 ) map { post =>
					Ok(Json.toJson(post))
				} recover {
					case ex: Exception =>
						BadRequest(Json.obj("msg" -> ex.getMessage))
				},
			authFailure =
				postService selectEnabledPosts(
					size = size.getOrElse(15),
					page = page.getOrElse(1),
					keyword = keyword,
					boardId = boardId
				) map { post =>
					Ok(Json.toJson(post))
				} recover {
					case ex: Exception =>
						BadRequest(Json.obj("msg" -> ex.getMessage))
				}
		)
	}
	
	def getThumbnails(size: Option[Int], boardId: Option[Int]): Action[AnyContent] = Action.async { implicit request =>
		postService selectThumbnails(size.getOrElse(10), boardId) map { thumbnails =>
			Ok(Json.toJson(thumbnails))
		}
	}
	
	def updatePost(postId: Int): Action[AnyContent] = Action.async { implicit request =>
		withAuth {
			withJson[PostRequestDTO] { post =>
				postService updatePost post.setPostId(postId) map {
					case Right(_) => Ok
					case Left(ContentTooLarge(length)) =>
						UnprocessableEntity(CONTENT_TOO_LARGE_JSON_MSG(length))
					case Left(TitleTooLarge(length)) =>
						UnprocessableEntity(TITLE_TOO_LARGE_JSON_MSG(length))
					case Left(EmptyPostId) =>
						BadRequest(Json.obj("msg" -> "postId가 비어있습니다."))
					case Left(PostNotExist) => NotFound
				} recover {
					case ex: Exception =>
						BadRequest(Json.obj("msg" -> ex.getMessage))
				}
			}
		}
	}
	
	def addSequence(postId: Int): Action[AnyContent] = Action.async { implicit request =>
		withAuth {
			postService addSequence postId map {
				case Some(_) => Ok
				case None => NotFound
			} recover {
				case ex: Exception =>
					BadRequest(Json.obj("msg" -> ex.getMessage))
			}
		}
	}
	
	def subSequence(postId: Int): Action[AnyContent] = Action.async { implicit request =>
		withAuth {
			postService subSequence postId map {
				case Some(_) => Ok
				case None => NotFound
			} recover {
				case ex: Exception =>
					BadRequest(Json.obj("msg" -> ex.getMessage))
			}
		}
	}
	
}
