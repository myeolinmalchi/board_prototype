package services

import common.validation.ValidationResultLib
import dto.{PostDTO, PostRequestDTO}
import scala.concurrent.Future
import services.PostService.PostFailure

trait PostService extends ValidationResultLib[Future] {

	def addPost(post: PostRequestDTO): Future[Either[PostFailure, Unit]]

}
object PostService {
	sealed abstract class PostFailure
	case class TitleLengthTooLarge(length: Int) extends PostFailure
	case class ContentLengthTooLarge(length: Int) extends PostFailure
	
	val TITLE_MAX_LENGTH = 200
	val CONTENT_MAX_LENGTH = 2000
}
