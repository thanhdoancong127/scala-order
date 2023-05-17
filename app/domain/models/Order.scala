package domain.models

import play.api.libs.json.{Json, OFormat}

import java.sql.Date

case class Order(id: Option[Long],
                 userId: Long,
                 orderDate: Date,
                 totalPrice: BigDecimal)

object Order {

  /**
   * Mapping to read/write an Order object as a JSON value.
   */
  implicit val format: OFormat[Order] = Json.format[Order]
}