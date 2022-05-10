package dto

import java.util.Date
import models.Tables.{PostImagesRow, Posts}
import play.api.libs.json.Json

case class PostDTO(postId: Int,
                   boardId: Int,
                   title: String,
                   thumbnail: String,
                   content: String,
                   sequence: Int,
                   addedDate: Date,
                   status: Boolean,
                   images: List[PostImageDTO] = Nil) {
	
	def setImages(images: List[PostImageDTO]): PostDTO =
		PostDTO(postId, boardId, title, thumbnail, content, sequence, addedDate, status, images)
	
}

case class PostResponseDTO(nowPage: Int,
                           pageCount: Int,
                           posts: List[PostDTO])

case class PostImageDTO(postId: Int,
                        postImageId: Int,
                        image: String,
                        sequence: Int)

case class PostRequestDTO(postId: Option[Int],
                          boardId: Int,
                          title: String,
                          thumbnail: String,
                          content: String,
                          status: Boolean,
                          images: List[String]) {
	def setPostId(postId: Int): PostRequestDTO =
		PostRequestDTO(Some(postId), boardId, title, thumbnail, content, status, images)
}

case class ThumbnailDTO(postId: Int,
                        boardId: Int,
                        thumbnail: String)

object PostImageDTO {
	implicit val imageWrites = Json.writes[PostImageDTO]
	implicit val imageReads = Json.reads[PostImageDTO]
	implicit val rowToDto = (row: PostImagesRow) =>
		PostImageDTO(row.postId, row.postId, row.image, row.sequence)
}

object PostDTO {
	implicit val postWrites = Json.writes[PostDTO]
	implicit val postReads = Json.reads[PostDTO]
	implicit val rowToDto = (row: Posts#TableElementType) =>
		PostDTO(
			row.postId,
			row.boardId,
			row.title,
			row.thumbnail,
			row.content,
			row.sequence,
			row.addedDate,
			row.status
		)
}

object PostResponseDTO {
	implicit val responseWrites = Json.writes[PostResponseDTO]
}

object PostRequestDTO {
	implicit val postRequestWrites = Json.writes[PostRequestDTO]
	implicit val postRequestReads = Json.reads[PostRequestDTO]
}

object ThumbnailDTO {
	implicit val thumbnailWrites = Json.writes[ThumbnailDTO]
	implicit val thumbnailReads = Json.reads[ThumbnailDTO]
	implicit val rowToDto = (row: Posts#TableElementType) =>
		ThumbnailDTO(row.postId, row.boardId, row.thumbnail)
}

