package models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.MySQLProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Admin.schema ++ Boards.schema ++ PostImages.schema ++ Posts.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Admin
   *  @param id Database column id SqlType(VARCHAR), Length(20,true)
   *  @param password Database column password SqlType(VARCHAR), Length(300,true)
   *  @param name Database column name SqlType(VARCHAR), Length(20,true), Default(None)
   *  @param email Database column email SqlType(VARCHAR), Length(100,true), Default(None)
   *  @param phone Database column phone SqlType(VARCHAR), Length(20,true), Default(None) */
  case class AdminRow(id: String, password: String, name: Option[String] = None, email: Option[String] = None, phone: Option[String] = None)
  /** GetResult implicit for fetching AdminRow objects using plain SQL queries */
  implicit def GetResultAdminRow(implicit e0: GR[String], e1: GR[Option[String]]): GR[AdminRow] = GR{
    prs => import prs._
    AdminRow.tupled((<<[String], <<[String], <<?[String], <<?[String], <<?[String]))
  }
  /** Table description of table admin. Objects of this class serve as prototypes for rows in queries. */
  class Admin(_tableTag: Tag) extends profile.api.Table[AdminRow](_tableTag, Some("board_prototype"), "admin") {
    def * = (id, password, name, email, phone) <> (AdminRow.tupled, AdminRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(password), name, email, phone)).shaped.<>({r=>import r._; _1.map(_=> AdminRow.tupled((_1.get, _2.get, _3, _4, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(VARCHAR), Length(20,true) */
    val id: Rep[String] = column[String]("id", O.Length(20,varying=true))
    /** Database column password SqlType(VARCHAR), Length(300,true) */
    val password: Rep[String] = column[String]("password", O.Length(300,varying=true))
    /** Database column name SqlType(VARCHAR), Length(20,true), Default(None) */
    val name: Rep[Option[String]] = column[Option[String]]("name", O.Length(20,varying=true), O.Default(None))
    /** Database column email SqlType(VARCHAR), Length(100,true), Default(None) */
    val email: Rep[Option[String]] = column[Option[String]]("email", O.Length(100,varying=true), O.Default(None))
    /** Database column phone SqlType(VARCHAR), Length(20,true), Default(None) */
    val phone: Rep[Option[String]] = column[Option[String]]("phone", O.Length(20,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table Admin */
  lazy val Admin = new TableQuery(tag => new Admin(tag))

  /** Entity class storing rows of table Boards
   *  @param boardId Database column board_id SqlType(INT), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(INT) */
  case class BoardsRow(boardId: Int, name: Int)
  /** GetResult implicit for fetching BoardsRow objects using plain SQL queries */
  implicit def GetResultBoardsRow(implicit e0: GR[Int]): GR[BoardsRow] = GR{
    prs => import prs._
    BoardsRow.tupled((<<[Int], <<[Int]))
  }
  /** Table description of table boards. Objects of this class serve as prototypes for rows in queries. */
  class Boards(_tableTag: Tag) extends profile.api.Table[BoardsRow](_tableTag, Some("board_prototype"), "boards") {
    def * = (boardId, name) <> (BoardsRow.tupled, BoardsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(boardId), Rep.Some(name))).shaped.<>({r=>import r._; _1.map(_=> BoardsRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column board_id SqlType(INT), AutoInc, PrimaryKey */
    val boardId: Rep[Int] = column[Int]("board_id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(INT) */
    val name: Rep[Int] = column[Int]("name")
  }
  /** Collection-like TableQuery object for table Boards */
  lazy val Boards = new TableQuery(tag => new Boards(tag))

  /** Entity class storing rows of table PostImages
   *  @param postId Database column post_id SqlType(INT)
   *  @param postImageId Database column post_image_id SqlType(INT), AutoInc, PrimaryKey
   *  @param image Database column image SqlType(LONGTEXT), Length(2147483647,true)
   *  @param sequence Database column sequence SqlType(INT) */
  case class PostImagesRow(postId: Int, postImageId: Int, image: String, sequence: Int)
  /** GetResult implicit for fetching PostImagesRow objects using plain SQL queries */
  implicit def GetResultPostImagesRow(implicit e0: GR[Int], e1: GR[String]): GR[PostImagesRow] = GR{
    prs => import prs._
    PostImagesRow.tupled((<<[Int], <<[Int], <<[String], <<[Int]))
  }
  /** Table description of table post_images. Objects of this class serve as prototypes for rows in queries. */
  class PostImages(_tableTag: Tag) extends profile.api.Table[PostImagesRow](_tableTag, Some("board_prototype"), "post_images") {
    def * = (postId, postImageId, image, sequence) <> (PostImagesRow.tupled, PostImagesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(postId), Rep.Some(postImageId), Rep.Some(image), Rep.Some(sequence))).shaped.<>({r=>import r._; _1.map(_=> PostImagesRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column post_id SqlType(INT) */
    val postId: Rep[Int] = column[Int]("post_id")
    /** Database column post_image_id SqlType(INT), AutoInc, PrimaryKey */
    val postImageId: Rep[Int] = column[Int]("post_image_id", O.AutoInc, O.PrimaryKey)
    /** Database column image SqlType(LONGTEXT), Length(2147483647,true) */
    val image: Rep[String] = column[String]("image", O.Length(2147483647,varying=true))
    /** Database column sequence SqlType(INT) */
    val sequence: Rep[Int] = column[Int]("sequence")

    /** Foreign key referencing Posts (database name FK__posts) */
    lazy val postsFk = foreignKey("FK__posts", postId, Posts)(r => r.postId, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table PostImages */
  lazy val PostImages = new TableQuery(tag => new PostImages(tag))

  /** Entity class storing rows of table Posts
   *  @param postId Database column post_id SqlType(INT), AutoInc, PrimaryKey
   *  @param boardId Database column board_id SqlType(INT)
   *  @param title Database column title SqlType(VARCHAR), Length(200,true)
   *  @param thumbnail Database column thumbnail SqlType(LONGTEXT), Length(2147483647,true)
   *  @param content Database column content SqlType(VARCHAR), Length(2000,true), Default()
   *  @param sequence Database column sequence SqlType(INT) */
  case class PostsRow(postId: Int, boardId: Int, title: String, thumbnail: String, content: String = "", sequence: Int)
  /** GetResult implicit for fetching PostsRow objects using plain SQL queries */
  implicit def GetResultPostsRow(implicit e0: GR[Int], e1: GR[String]): GR[PostsRow] = GR{
    prs => import prs._
    PostsRow.tupled((<<[Int], <<[Int], <<[String], <<[String], <<[String], <<[Int]))
  }
  /** Table description of table posts. Objects of this class serve as prototypes for rows in queries. */
  class Posts(_tableTag: Tag) extends profile.api.Table[PostsRow](_tableTag, Some("board_prototype"), "posts") {
    def * = (postId, boardId, title, thumbnail, content, sequence) <> (PostsRow.tupled, PostsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(postId), Rep.Some(boardId), Rep.Some(title), Rep.Some(thumbnail), Rep.Some(content), Rep.Some(sequence))).shaped.<>({r=>import r._; _1.map(_=> PostsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column post_id SqlType(INT), AutoInc, PrimaryKey */
    val postId: Rep[Int] = column[Int]("post_id", O.AutoInc, O.PrimaryKey)
    /** Database column board_id SqlType(INT) */
    val boardId: Rep[Int] = column[Int]("board_id")
    /** Database column title SqlType(VARCHAR), Length(200,true) */
    val title: Rep[String] = column[String]("title", O.Length(200,varying=true))
    /** Database column thumbnail SqlType(LONGTEXT), Length(2147483647,true) */
    val thumbnail: Rep[String] = column[String]("thumbnail", O.Length(2147483647,varying=true))
    /** Database column content SqlType(VARCHAR), Length(2000,true), Default() */
    val content: Rep[String] = column[String]("content", O.Length(2000,varying=true), O.Default(""))
    /** Database column sequence SqlType(INT) */
    val sequence: Rep[Int] = column[Int]("sequence")

    /** Foreign key referencing Boards (database name FK_posts_boards) */
    lazy val boardsFk = foreignKey("FK_posts_boards", boardId, Boards)(r => r.boardId, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)

    /** Uniqueness Index over (sequence) (database name sequence) */
    val index1 = index("sequence", sequence, unique=true)
  }
  /** Collection-like TableQuery object for table Posts */
  lazy val Posts = new TableQuery(tag => new Posts(tag))
}
