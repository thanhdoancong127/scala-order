package domain.tables

import domain.models.OrderDetails
import slick.jdbc.PostgresProfile.api._

class OrderDetailsTable(tag: Tag) extends Table[OrderDetails](tag, Some("scalaorder"), "order_details") {

  /** The ID column, which is the primary key and auto-incremented */
  def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)

  /** The order ID column */
  def orderId = column[Long]("order_id")

  /** The product ID column */
  def productId = column[Long]("product_id")

  /** The quantity column */
  def quantity = column[Int]("quantity")

  /** The price column */
  def price = column[BigDecimal]("price")

  /** The table's default projection */
  def * = (id, orderId, productId, quantity, price) <> ((OrderDetails.apply _).tupled, OrderDetails.unapply)

  // Add any additional constraints or foreign keys here if needed
}
