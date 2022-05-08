package services

import common.validation.ValidationResultLib
import dto.{PostDTO, PostRequestDTO, PostResponseDTO, ThumbnailDTO}
import javax.inject.{Inject, Singleton}
import models.PostModel
import scala.concurrent.{ExecutionContext, Future}
import services.PostService.{CONTENT_MAX_LENGTH, ContentLengthTooLarge, PostFailure, TITLE_MAX_LENGTH, TitleLengthTooLarge}

@Singleton
class PostServiceImpl @Inject() ()(implicit ex: ExecutionContext, postModel: PostModel)
		extends PostService with ValidationResultLib[Future] {
	
	implicit class PostValidator(post: PostRequestDTO) {
		private def checkTitleLength: ValidationResult[PostFailure, Unit] =
			ValidationResult.ensure (
				post.title.length > TITLE_MAX_LENGTH,
				onFailure = TitleLengthTooLarge(post.title.length)
			)
			
		private def checkContentLength: ValidationResult[PostFailure, Unit] =
			ValidationResult.ensure (
				post.content.length > CONTENT_MAX_LENGTH,
				onFailure = ContentLengthTooLarge(post.content.length)
			)
			
		def validation: Future[Either[PostFailure, Int]] = {
			val result = for {
				_ <- checkTitleLength
				_ <- checkContentLength
			} yield ()
			result.onSuccess (
				postModel insert post
			)
		}
	}
	
	override def addPost(post: PostRequestDTO): Future[Either[PostService.PostFailure, Int]] =
		post.validation
	
	override def getPost(postId: Int): Future[Option[PostDTO]] =
		postModel select postId
	
	override def selectPosts(size: Int,
							 page: Int,
							 keyword: Option[String],
							 boardId: Option[Int]): Future[PostResponseDTO] =
		for {
			posts <- postModel selectPosts (size, page, keyword, boardId)
			count <- postModel postCount (size, page, keyword, boardId)
		} yield PostResponseDTO(page, count, posts)
	
	
	override def selectThumbnails(size: Int, boardId: Option[Int]): Future[List[ThumbnailDTO]] =
		postModel selectThumbnails boardId
	
}
