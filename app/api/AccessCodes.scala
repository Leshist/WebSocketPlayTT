package api


object AccessCodes {
  sealed abstract class AccessCode
  case object Allowed extends AccessCode
  case object Forbidden extends AccessCode
}
