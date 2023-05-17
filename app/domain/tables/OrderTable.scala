package domain.tables

import slick.jdbc.PostgresProfile.api._
import domain.models.Order

import java.sql.Date


class OrderTable(tag: Tag) extends Table[Order](tag, Some("scalaorder"), "orders") {

  /** ID column, primary key, auto-incremented */
  def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)

  /** User ID column */
  def userId = column[Long]("user_id")

  /** Order date column */
  def orderDate = column[Date]("order_date")

  /** Total price column */
  def totalPrice = column[BigDecimal]("total_price")

  /** Projection definition */
  def * = (id, userId, orderDate, totalPrice)  <> ((Order.apply _).tupled, Order.unapply)

  /** Define foreign key constraint with "users" table */
}
