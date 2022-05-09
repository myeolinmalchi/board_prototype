package services

import cats.data.OptionT
import common.validation.ValidationResultLib
import dto.AdminDTO
import javax.inject.Inject
import models.AdminModel
import scala.concurrent.{ExecutionContext, Future}
import common.encryption.SHA256.encrypt

class AccountServiceImpl @Inject() ()(implicit ec: ExecutionContext, adminModel: AdminModel)
	extends AccountService with ValidationResultLib[Future]{
	
	implicit class AccountValidator(admin: AdminDTO) {
		import AccountService._
		private def checkPattern(str: String, value: Value): ValidationResult[ValidationFailure, Unit] =
			ValidationResult.ensure(
				str.matches(value.pattern),
				onFailure = ValidationFailure(value.patternMsg)
			)
		
		private def validId: ValidationResult[ValidationFailure, Unit] =
			for {
				_ <- checkPattern(admin.id, ID)
				_ <- ValidationResult.ensureM (
					adminModel checkIdNotExists admin.id,
					onFailure = ValidationFailure(ID.overlapMsg)
				)
			} yield ()
		
		private def validPw: ValidationResult[ValidationFailure, Unit] =
			checkPattern(admin.pw, PW)
			
		private def validName: ValidationResult[ValidationFailure, Unit] = admin.name map { name =>
			checkPattern(name, NAME)
		} getOrElse ValidationResult.successful()
		
		private def validEmail: ValidationResult[ValidationFailure, Unit] = admin.email map { email =>
			for {
				_ <- checkPattern(email, EMAIL)
				_ <- ValidationResult.ensureM (
					adminModel checkEmailNotExists email,
					onFailure = ValidationFailure(EMAIL.overlapMsg)
				)
			} yield ()
		} getOrElse ValidationResult.successful()
		
		private def validPhone: ValidationResult[ValidationFailure, Unit] = admin.phone map { phone =>
			for {
				_ <- checkPattern(phone, PHONE)
				_ <- ValidationResult.ensureM (
					adminModel checkPhoneNotExists phone,
					onFailure = ValidationFailure(PHONE.overlapMsg)
				)
			} yield ()
		} getOrElse ValidationResult.successful()
		
		def validationAndInsert: Future[Either[ValidationFailure, Int]] =
			(for {
				_ <- validId
				_ <- validPw
				_ <- validName
				_ <- validEmail
				_ <- validPhone
			} yield ()).onSuccess(adminModel insert admin)
	}
	
	override def login(implicit admin: AdminDTO): OptionT[Future, Boolean] =
		for {
			correctPw <- OptionT(adminModel selectPassword admin.id)
			enteredPw <- OptionT.some[Future](admin.pw)
		} yield correctPw.equals(encrypt(enteredPw))
	
	override def regist(implicit admin: AdminDTO): Future[Either[ValidationFailure, Int]] =
		admin.validationAndInsert
		
}
