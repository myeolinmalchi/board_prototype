package services

import com.google.inject.ImplementedBy
import common.validation.ValidationResultLib
import dto.{PostDTO, PostRequestDTO, PostResponseDTO, ThumbnailDTO}
import scala.concurrent.Future
import services.PostService.PostFailure

@ImplementedBy(classOf[PostServiceImpl])
trait PostService extends ValidationResultLib[Future] {
	def addPost(post: PostRequestDTO): Future[Either[PostFailure, Int]]
	
	def getPost(postId: Int): Future[Option[PostDTO]]
	
	def selectPosts(size: Int,
	                page: Int,
	                keyword: Option[String],
	                boardId: Option[Int]): Future[PostResponseDTO]
	
	def selectThumbnails(size: Int, boardId: Option[Int]): Future[List[ThumbnailDTO]]
	
	def updatePost(post: PostRequestDTO): Future[Either[PostFailure, Int]]
	
	def addSequence(postId: Int): Future[Option[Int]]
	
	def subSequence(postId: Int): Future[Option[Int]]
	
}

object PostService {
	sealed abstract class PostFailure
	
	case class TitleTooLarge(length: Int) extends PostFailure
	case class ContentTooLarge(length: Int) extends PostFailure
	case object EmptyPostId extends PostFailure
	case object PostNotExist extends PostFailure
	
	val TITLE_MAX_LENGTH = 200
	val CONTENT_MAX_LENGTH = 2000
}
