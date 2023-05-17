package controllers.products

import controllers.user.UserResource.dateFormat
import play.api.libs.json.{Json, OFormat}
import domain.models.Product

import java.sql.Date
import java.text.SimpleDateFormat

/**
 * DTO for displaying post information.
 */
case class ProductResource(id: Long, productName: String, price: BigDecimal, expirationDate: String)

object ProductResource {

  /**
   * Mapping to read/write a PostResource out as a JSON value.
   */
  implicit val format: OFormat[ProductResource] = Json.format[ProductResource]
  val dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

  def fromProduct(product: Product): ProductResource = ProductResource(product.id.getOrElse(-1), product.productName, product.price, dateFormat.format(product.expirationDate))
}
