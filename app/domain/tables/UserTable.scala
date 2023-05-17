package domain.tables

import domain.models.User
import slick.jdbc.PostgresProfile.api._

import java.sql.Date

class UserTable(tag: Tag) extends Table[User](tag, Some("scalaorder"), "users") {

  /** The ID column, which is the primary key, and auto incremented */
  def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc, O.Unique)

  /** The email column */
  def email = column[String]("email", O.Unique)

  /** The role column */
  def role = column[String]("role")

  /** The first name column */
  def firstName = column[String]("first_name")

  /** The last name column */
  def lastName = column[String]("last_name")

  /** The password column */
  def password = column[Option[String]]("password")

  /** The password column */
  def birthday = column[Option[Date]]("birthday")

  /** The address column */
  def address = column[Option[String]]("address")

  /** The address column */
  def phoneNumber = column[Option[String]]("phone_number")

  /**
   * This is the table's default "projection".
   * It defines how the columns are converted to and from the User object.
   * In this case, we are simply passing the id, name, email and password parameters to the User case classes
   * apply and unapply methods.
   */
  def * = (id, email, role, firstName, lastName, password, birthday, address, phoneNumber) <> ((User.apply _).tupled, User.unapply)
}
