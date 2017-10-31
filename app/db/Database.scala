package db

// todo: real db?
class Database {
  case class Credentials(login: String, password: String)
  val adminCredentials = List(Credentials("user1234", "password1234"))
  def isAdmin(username: String, password: String): Boolean = adminCredentials.contains(Credentials(username, password))
}
