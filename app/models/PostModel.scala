package models

import com.google.inject.ImplementedBy
import dto.{PostDTO, PostRequestDTO, ThumbnailDTO}
import scala.concurrent.Future

@ImplementedBy(classOf[PostModelImpl])
trait PostModel {

	def insert(postDto: PostRequestDTO): Future[Int]
	def select(postId: Int): Future[Option[PostDTO]]
	def selectWithPagination(boardId: Int, size: Int, page: Int): Future[List[PostDTO]]
	def searchWithPagination(boardId: Int, size: Int, page: Int, keyword: String): Future[List[PostDTO]]
	def selectWithPaginationInAllBoard(size: Int, page: Int): Future[List[PostDTO]]
	def searchWithPaginationInAllBoard(size: Int, page: Int, keyword: String): Future[List[PostDTO]]
	def selectThumbnails(boardId: Int): Future[List[ThumbnailDTO]]
	def delete(postId: Int): Future[Int]
	def update(postDTO: PostRequestDTO): Future[Int]
	
}
