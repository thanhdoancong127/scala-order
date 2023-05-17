package controllers.order

import domain.models.{OrderDetails}
import play.api.libs.json.{Json, OFormat}

import java.text.SimpleDateFormat

/**
 * DTO for displaying OrderDetails information.
 */
case class OrderDetailsResource(id: Long, productId: Long, quantity: Int, price: BigDecimal)

object OrderDetailsResource {

  /**
   * Mapping to read/write a OrderDetailsResource out as a JSON value.
   */
  implicit val format: OFormat[OrderDetailsResource] = Json.format[OrderDetailsResource]
  val dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    def fromOrderDetails(orderDetail: OrderDetails): OrderDetailsResource =
        OrderDetailsResource(orderDetail.id, orderDetail.productId, orderDetail.quantity, orderDetail.price)
}
