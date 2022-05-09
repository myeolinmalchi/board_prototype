package services

import cats.data.OptionT
import com.google.inject.ImplementedBy
import common.validation.ValidationResultLib
import dto.AdminDTO
import scala.concurrent.Future

@ImplementedBy(classOf[AccountServiceImpl])
trait AccountService extends ValidationResultLib[Future]{
	def login(implicit admin: AdminDTO): OptionT[Future, Boolean]
	def regist(implicit admin: AdminDTO): Future[Either[ValidationFailure, Int]]
}

object AccountService extends Enumeration {
	val ID, PW, NAME, EMAIL, PHONE = Value
	implicit class EnumMethod(enums: Value) {
		def pattern: String = enums match {
			case ID => "^[a-z]+[a-z0-9]{5,19}$"
			case PW => "^(?=.*\\d)(?=.*[a-zA-Z])[0-9a-zA-Z]{8,16}$"
			case NAME => "^[ㄱ-힣]+$"
			case EMAIL => "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$"
			case PHONE => "^\\d{3}-\\d{3,4}-\\d{4}$"
		}
		def patternMsg: String = enums match {
			case ID => "id"
			case PW => "pw"
			case NAME => "name"
			case EMAIL => "email"
			case PHONE => "phone"
		}
		def overlapMsg: String = enums match {
			case ID => "이미 존재하는 계정입니다."
			case EMAIL => "이미 사용중인 이메일입니다."
			case PHONE => "이미 사용중인 전화번호입니다."
			case _ => ""
		}
	}
}