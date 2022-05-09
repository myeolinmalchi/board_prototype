package models

import dto.{PostDTO, PostImageDTO, PostRequestDTO, ThumbnailDTO}
import javax.inject.{Inject, Singleton}
import models.Tables.{PostImages, PostImagesRow, Posts, PostsRow}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.dbio.{DBIO, DBIOAction, Effect, NoStream}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

@Singleton
class PostModelImpl @Inject()(val dbConfigProvider: DatabaseConfigProvider)
                             (implicit ec: ExecutionContext)
	extends HasDatabaseConfigProvider[JdbcProfile] with PostModel {
	
	def toDto[T, R](xs: Seq[T])(g: R => DBIOAction[R, NoStream, Effect.All])
	               (implicit f: T => R): DBIOAction[List[R], NoStream, Effect.All] =
		DBIO.sequence(xs.map(f andThen g).toList)
	
	def toDto[T, R](xo: Option[T])(g: R => DBIOAction[R, NoStream, Effect.All])
	               (implicit f: T => R): DBIOAction[Option[R], NoStream, Effect.All] =
		DBIO.sequenceOption(xo map (f andThen g))
	
	override def insert(post: PostRequestDTO): Future[Int] = db run {
		for {
			lastSeq <- Posts
				.map(_.sequence)
				.max
				.result
			postId <- Posts.map { post => (
				post.boardId, post.sequence, post.title, post.content, post.thumbnail
			)} returning Posts.map(_.postId) += (
				post.boardId,
				lastSeq.getOrElse(0) + 1,
				post.title,
				post.content,
				post.thumbnail,
			)
			aff <- PostImages ++= post.images.zipWithIndex.map {
				case (image, index) =>
					PostImagesRow(postId, 0, image, index + 1)
			}
		} yield aff.getOrElse(0)
	}.transactionally
	
	override def select(postId: Int): Future[Option[PostDTO]] = db run {
		for {
			postOption <- Posts.filter(_.postId === postId).result.headOption
			postWithImagesOption <- toDto(postOption) { post: PostDTO =>
				for {
					images <- PostImages.filter(_.postId === postId).result
					imageDtoList = images.map(PostImageDTO.rowToDto).toList
				} yield post.setImages(imageDtoList)
			}
		} yield postWithImagesOption
	}
	
	private val postsQuery = (size: Int,
	                          page: Int,
	                          keyword: Option[String],
	                          boardId: Option[Int]) =>
		Posts
			.filterOpt(boardId)(_.boardId === _)
			.filterOpt(keyword)((post, keyword) => post.title like s"%$keyword%")
			.sorted(_.sequence)
			.drop((page - 1) * size)
			.take(size)
	
	override def selectPosts(size: Int,
	                         page: Int,
	                         keyword: Option[String],
	                         boardId: Option[Int]): Future[List[PostDTO]] = db run {
		for {
			posts <- postsQuery(size, page, keyword, boardId).result
		} yield (posts map PostDTO.rowToDto).toList
	}
	
	override def postCount(size: Int,
	                       page: Int,
	                       keyword: Option[String],
	                       boardId: Option[Int]): Future[Int] =
		db run postsQuery(size, page, keyword, boardId).size.result
	
	
	override def selectThumbnails(size: Int, boardId: Option[Int]): Future[List[ThumbnailDTO]] = db run {
		for {
			posts <- Posts
				.filterOpt(boardId)(_.boardId === _)
				.sortBy(_.sequence.desc)
				.take(size)
				.result
		} yield (posts map ThumbnailDTO.rowToDto).toList
	}
	
	override def delete(postId: Int): Future[Int] =
		db run Posts.filter(_.postId === postId).delete
	
	override def update(post: PostRequestDTO): Future[Int] = db run {
		for {
			aff1 <- Posts
				.map(p => (p.boardId, p.title, p.content, p.thumbnail))
				.update((post.boardId, post.title, post.content, post.thumbnail))
			aff2 <- PostImages
				.filter(_.postId === post.postId)
				.delete
			aff3 <- PostImages ++= post.images.zipWithIndex.map {
				case (image, index) =>
					PostImagesRow(
						postId = post.postId.getOrElse(throw new Exception("postId가 비어있습니다.")),
						postImageId = 0,
						image = image,
						sequence = index + 1
					)
			}
		} yield aff1 + aff2 + aff3.getOrElse(0)
	}.transactionally
	
	override def checkPostExists(postId: Int): Future[Boolean] = db run {
		for {
			postOption <- Posts.filter(_.postId === postId).result.headOption
		} yield postOption.isDefined
	}
	
}
