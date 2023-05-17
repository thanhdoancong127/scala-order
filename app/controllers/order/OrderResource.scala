package controllers.order

import controllers.products.ProductResource
import domain.models.Order
import play.api.libs.json.{Json, OFormat}

import java.text.SimpleDateFormat

/**
 * DTO for displaying post information.
 */
case class OrderResource(id: Long, userId: Long, totalPrice: BigDecimal, orderDate: String, orderItems: Seq[OrderDetailsResource])

object OrderResource {

  /**
   * Mapping to read/write a PostResource out as a JSON value.
   */
  implicit val format: OFormat[OrderResource] = Json.format[OrderResource]
  val dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

  def fromOrder(order: Order): OrderResource =
      OrderResource(order.id.getOrElse(-1), order.userId, order.totalPrice, dateFormat.format(order.orderDate), List[OrderDetailsResource]())
}
