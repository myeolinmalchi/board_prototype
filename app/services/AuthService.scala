package services

import com.google.inject.ImplementedBy
import play.api.mvc.{AnyContent, Request, Result}
import scala.concurrent.Future

@ImplementedBy(classOf[AuthServiceImpl])
trait AuthService {
	def withAuth(f: => Future[Result])
	            (implicit request: Request[AnyContent]): Future[Result]
	
	def withAuth(authSuccess: => Future[Result],
	             authFailure: => Future[Result])
	            (implicit request: Request[AnyContent]): Future[Result]
	
}

object AuthService {
	
	sealed abstract class AuthResult
	case class AuthSuccess[T](value: T) extends AuthResult // 인증 성공
	sealed abstract class AuthFailure extends AuthResult
	
	case object InvalidToken extends AuthFailure // 유효하지 않은 토큰
	case object ExpiredToken extends AuthFailure // 유효기간이 지난 토큰
	case object IncorrectAuth extends AuthFailure // 사용자 권한이 아님
	case object NotExistID extends AuthFailure // db 상에 존재하지 않는 ID
	case object NoToken extends AuthFailure // 토큰이 없음(사용자가 아님)
}
