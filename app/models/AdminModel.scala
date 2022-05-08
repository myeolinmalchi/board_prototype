package models

import com.google.inject.ImplementedBy
import dto.AdminDTO
import scala.concurrent.Future

@ImplementedBy(classOf[AdminModelImpl])
trait AdminModel {
	
	def insert(admin: AdminDTO): Future[Int]
	def select(id: String): Future[Option[AdminDTO]]
	def selectPassword(id: String): Future[Option[String]]
	def delete(id: String): Future[Int]
	def update(admin: AdminDTO): Future[Int]
	
}
