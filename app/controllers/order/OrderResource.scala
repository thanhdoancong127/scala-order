package controllers.order

import domain.models.{Order, OrderDetails}
import play.api.libs.json.{JsArray, JsBoolean, JsNull, JsNumber, JsObject, JsString, JsValue, Json, OFormat}

import java.text.SimpleDateFormat
import scala.concurrent.Future
import scala.util.Try

/**
 * DTO for displaying post information.
 */
case class OrderResource(id: Long, userId: Long, totalPrice: BigDecimal, orderDate: String)

object OrderResource {

  /**
   * Mapping to read/write a PostResource out as a JSON value.
   */
  implicit val format: OFormat[OrderResource] = Json.format[OrderResource]
  val dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

  def fromOrder(order: Order): OrderResource =
      OrderResource(order.id.getOrElse(-1), order.userId, order.totalPrice, dateFormat.format(order.orderDate))
}
