package models

import com.google.inject.ImplementedBy
import dto.{PostDTO, PostRequestDTO, ThumbnailDTO}
import scala.concurrent.Future

@ImplementedBy(classOf[PostModelImpl])
trait PostModel {
	
	def insert(postDto: PostRequestDTO): Future[Int]
	
	def select(postId: Int): Future[Option[PostDTO]]
	
	def selectPosts(size: Int,
	                page: Int,
	                keyword: Option[String],
	                boardId: Option[Int]): Future[List[PostDTO]]
	
	def selectEnabledPosts(size: Int,
	                       page: Int,
	                       keyword: Option[String],
	                       board: Option[Int]): Future[List[PostDTO]]
	
	def postCount(size: Int,
	              page: Int,
	              keyword: Option[String],
	              boardId: Option[Int]): Future[Int]
	
	def enabledPostCount(size: Int,
	                     page: Int,
	                     keyword: Option[String],
	                     boardId: Option[Int]): Future[Int]
	
	def selectThumbnails(size: Int, boardId: Option[Int]): Future[List[ThumbnailDTO]]
	
	def delete(postId: Int): Future[Int]
	
	def update(postDTO: PostRequestDTO): Future[Int]
	
	def addSequence(postId: Int): Future[Option[Int]]
	
	def subSequence(postId: Int): Future[Option[Int]]
	
	def checkPostExists(postId: Int): Future[Boolean]
	
	
}
