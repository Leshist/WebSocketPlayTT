package api


object AccessLevels {
  sealed abstract class AccessLevel
  case object User extends AccessLevel {
    override def toString: String = "user"
  }
  case object Admin extends AccessLevel {
    override def toString: String = "admin"
  }
}
