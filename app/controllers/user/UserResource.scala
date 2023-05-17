package controllers.user

import domain.models.User
import play.api.libs.json.{Json, OFormat}

import java.sql.Date
import java.text.SimpleDateFormat

/**
 * DTO for displaying post information.
 */
case class UserResource(id: Long, email: String, rolse: String,firstName: String, lastName: String, birthday: String, address: Option[String], phoneNumber: Option[String])

object UserResource {

  /**
   * Mapping to read/write a PostResource out as a JSON value.
   */
  implicit val format: OFormat[UserResource] = Json.format[UserResource]

  val dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

  def fromUser(user: User): UserResource = UserResource(user.id.getOrElse(-1), user.email, user.role, user.firstName, user.lastName, if(user.birthDay.nonEmpty) dateFormat.format(user.birthDay.get) else "", user.address, user.phoneNumber)
}
