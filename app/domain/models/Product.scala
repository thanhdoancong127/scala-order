package domain.models

import play.api.libs.json.{Json, OFormat}

import java.sql.Date

case class Product(id: Option[Long],
                   productName: String,
                   price: BigDecimal,
                   expirationDate: Date)

object Product {

  /**
   * Mapping to read/write a Product out as a JSON value.
   */
  implicit val format: OFormat[Product] = Json.format[Product]
}
