package services

import common.validation.ValidationResultLib
import dto.PostRequestDTO
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
			
		def validation: ValidationResult[PostFailure, Unit] =
			for {
				_ <- checkTitleLength
				_ <- checkContentLength
			} yield ()
	}
	
	override def addPost(post: PostRequestDTO): Future[Either[PostService.PostFailure, Unit]] =
		post.validation.value
		
	def
}
