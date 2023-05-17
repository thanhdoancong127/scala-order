package services

import com.google.inject.{ImplementedBy, Inject, Singleton}
import domain.dao.{OrderDao, OrderDetailDao}
import domain.models.{Order, OrderDetails}
import controllers.order.{OrderDetailsResource, OrderPostRequest, OrderResource}

import java.sql.Date
import java.util.Calendar
import scala.concurrent.{Await, ExecutionContext, Future}

/**
 * OrderService.
 */
@ImplementedBy(classOf[OrdererviceImpl])
trait OrderService{

    /**
     * Finds a Order by id.
     *
     * @param id The Order id to find.
     * @return The found Order or None if no Order for the given id could be found.
     */
    def find(id: Long): Future[Option[OrderResource]]

    /**
     * List all Orders.
     *
     * @return All existing Orders.
     */
    def listAll(): Future[Iterable[OrderResource]]

    /**
     * Saves a Order.
     *
     * @param Order The Order to save.
     * @return The saved Order.
     */
    def save(orderPostRequest: OrderPostRequest): Future[OrderResource]

    /**
     * Updates a Order.
     *
     * @param Order The Order to update.
     * @return The updated Order.
     */
    def update(Order: Order): Future[Order]

    /**
     * Deletes a Order
     * @param id The Order's id to delete.
     * @return The deleted Order.
     */
    def delete(id: Long): Future[Int]
}

/**
 * Handles actions to Orders.
 *
 * @param OrderDao The Order DAO implementation.
 * @param ex      The execution context.
 */
@Singleton
class OrdererviceImpl @Inject()(orderDao: OrderDao,
                                 orderDetailDao: OrderDetailDao)(implicit ex: ExecutionContext) extends OrderService {

    override def find(id: Long): Future[Option[OrderResource]] =
        orderDao.find(id).map {
            case Some(order) => Some(getOrderResponseAndItsItems(order))
            case None => None
        }

    override def listAll(): Future[Iterable[OrderResource]] = orderDao.listAll().map(orders =>
        orders.map(order =>
            getOrderResponseAndItsItems(order)))

    override def save(orderPostRequest: OrderPostRequest): Future[OrderResource] = {
        var totalPrice: Double = 0;
        var orderDetails = orderPostRequest.orderItemsRequest.map(orderItem => {
            totalPrice = totalPrice + orderItem.price * orderItem.quantity
            OrderDetails(
                None,
                0,
                orderItem.productId,
                orderItem.quantity,
                orderItem.price
            )
        })
        val order = Order(None, orderPostRequest.userId, new java.sql.Date(Calendar.getInstance().getTime().getTime()), totalPrice)
        val savedOrder = orderDao.save(order)
        savedOrder.map(order => {
            var orderItemsResponse = List[OrderDetailsResource]()
            orderDetails = orderDetails.map(orderDetail => {
                orderItemsResponse = orderItemsResponse.appended(
                    OrderDetailsResource.fromOrderDetails(orderDetail)
                )
                orderDetail.copy(orderId = order.id.get)
            })
            orderDetailDao.saveAll(orderDetails)

            OrderResource.fromOrder(order).copy(orderItems = orderItemsResponse)
        })
    }

    override def update(Order: Order): Future[Order] = orderDao.update(Order)

    override def delete(id: Long): Future[Int] = orderDao.delete(id)

    private def getOrderResponseAndItsItems(order: Order): OrderResource = {
        val listOrderDetail = Await.result(
            orderDetailDao.findAllByOrderId(order.id.get),
            scala.concurrent.duration.Duration.Inf
        )
        var orderItemsResponse: Seq[OrderDetailsResource] = listOrderDetail.map(orderDetail =>
            OrderDetailsResource.fromOrderDetails(orderDetail)
        )
        OrderResource.fromOrder(order).copy(orderItems = orderItemsResponse)
    }
}


