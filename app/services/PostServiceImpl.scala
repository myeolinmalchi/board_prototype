package services

import common.validation.ValidationResultLib
import dto.{PostDTO, PostRequestDTO, PostResponseDTO, ThumbnailDTO}
import javax.inject.{Inject, Singleton}
import models.PostModel
import scala.concurrent.{ExecutionContext, Future}
import services.PostService._

@Singleton
class PostServiceImpl @Inject()()(implicit ex: ExecutionContext, postModel: PostModel)
	extends PostService with ValidationResultLib[Future] {
	
	implicit class PostValidator(post: PostRequestDTO) {
		private def checkTitleLength: ValidationResult[PostFailure, Unit] =
			ValidationResult.ensure(
				post.title.length <= TITLE_MAX_LENGTH,
				onFailure = TitleTooLarge(post.title.length)
			)
		
		private def checkContentLength: ValidationResult[PostFailure, Unit] =
			ValidationResult.ensure(
				post.content.length <= CONTENT_MAX_LENGTH,
				onFailure = ContentTooLarge(post.content.length)
			)
		
		private def checkPostIdEmpty: ValidationResult[PostFailure, Unit] =
			ValidationResult.ensure(
				post.postId.isDefined,
				onFailure = EmptyPostId
			)
		
		private def checkPostExists: ValidationResult[PostFailure, Unit] =
			ValidationResult.ensureM(
				postModel checkPostExists post.postId.getOrElse(0),
				onFailure = PostNotExist
			)
		
		def validationForInserting: Future[Either[PostFailure, Int]] =
			(for {
				_ <- checkTitleLength
				_ <- checkContentLength
			} yield ()) onSuccess (postModel insert post)
		
		def validationForUpdating: Future[Either[PostFailure, Int]] =
			(for {
				_ <- checkPostIdEmpty
				_ <- checkPostExists
				_ <- checkTitleLength
				_ <- checkContentLength
			} yield ()) onSuccess (postModel update post)
	}
	
	override def addPost(post: PostRequestDTO): Future[Either[PostService.PostFailure, Int]] =
		post.validationForInserting
	
	override def getPost(postId: Int): Future[Option[PostDTO]] =
		postModel select postId
	
	override def selectPosts(size: Int,
	                         page: Int,
	                         keyword: Option[String],
	                         boardId: Option[Int]): Future[PostResponseDTO] =
		for {
			posts <- postModel selectPosts(size, page, keyword, boardId)
			count <- postModel postCount(size, page, keyword, boardId)
		} yield PostResponseDTO(page, count / size + 1, posts)
	
	override def selectThumbnails(size: Int, boardId: Option[Int]): Future[List[ThumbnailDTO]] =
		postModel selectThumbnails(size, boardId)
	
	override def updatePost(post: PostRequestDTO): Future[Either[PostFailure, Int]] =
		post.validationForUpdating
	
	override def addSequence(postId: Int): Future[Option[Int]] =
		postModel addSequence postId
	
	override def subSequence(postId: Int): Future[Option[Int]] =
		postModel subSequence postId
	
	
}
