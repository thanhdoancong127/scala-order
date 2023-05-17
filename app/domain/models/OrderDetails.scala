package domain.models

import play.api.libs.json.{Json, OFormat}

case class OrderDetails(id: Option[Long],
                        orderId: Long,
                        productId: Long,
                        quantity: Int,
                        price: BigDecimal)

object OrderDetails {

  /**
   * Mapping to read/write an OrderDetails object as a JSON value.
   */
  implicit val format: OFormat[OrderDetails] = Json.format[OrderDetails]
}
