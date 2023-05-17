package domain.tables

import domain.models.Product

import slick.jdbc.PostgresProfile.api._

import java.sql.Date

class ProductTable(tag: Tag) extends Table[Product](tag, Some("scalaorder"), "products") {

  /** The ID column, which is the primary key and auto-incremented */
  def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)

  /** The product name column */
  def productName = column[String]("product_name")

  /** The price column */
  def price = column[BigDecimal]("price")

  /** The expiration date column */
  def expDate = column[Date]("exp_date")

  /** The table's default projection */
  def * = (id, productName, price, expDate) <> ((Product.apply _).tupled, Product.unapply)

  // Add any additional constraints or foreign keys here if needed
}
