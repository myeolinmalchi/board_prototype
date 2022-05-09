package models

import common.encryption.SHA256._
import dto.AdminDTO
import javax.inject.Inject
import models.Tables.{Admin, AdminRow}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

class AdminModelImpl @Inject()(val dbConfigProvider: DatabaseConfigProvider)
                              (implicit ec: ExecutionContext)
	extends HasDatabaseConfigProvider[JdbcProfile] with AdminModel {
	
	override def insert(admin: AdminDTO): Future[Int] =
		db run (Admin += AdminRow(
			id = admin.id,
			password = encrypt(admin.pw),
			name = admin.name,
			email = admin.email,
			phone = admin.phone
		))
	
	override def select(id: String): Future[Option[AdminDTO]] = db run {
		for {
			adminOption <- Admin.filter(_.id === id).result.headOption
		} yield adminOption map AdminDTO.rowToDto
	}
	
	override def selectPassword(id: String): Future[Option[String]] =
		db run Admin.filter(_.id === id).map(_.password).result.headOption
	
	override def delete(id: String): Future[Int] =
		db run Admin.filter(_.id === id).delete
	
	override def checkAccountExists(id: String): Future[Boolean] = db run {
		for {
			idOption <- Admin.withFilter(_.id === id).result.headOption
		} yield idOption.isDefined
	}
	
	def checkNotExists(f: Admin => Rep[Boolean]): Future[Boolean] = db run {
		for {
			option <- Admin.withFilter(f).result.headOption
		} yield option.isEmpty
	}
	
	override def checkIdNotExists(id: String): Future[Boolean] =
		checkNotExists(_.id === id)
	
	override def checkEmailNotExists(email: String): Future[Boolean] =
		checkNotExists(a => a.email.getOrElse("") === email)
	
	override def checkPhoneNotExists(phone: String): Future[Boolean] =
		checkNotExists(a => a.phone.getOrElse("") === phone)
		
	
	override def update(admin: AdminDTO): Future[Int] = ???
}
