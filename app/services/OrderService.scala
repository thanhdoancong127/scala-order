package services

import com.google.inject.{ImplementedBy, Inject, Singleton}
import domain.dao.OrderDao
import domain.models.{Order, OrderDetails}

import scala.concurrent.{ExecutionContext, Future}

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
    def find(id: Long): (Future[Option[Order]], Future[Iterable[OrderDetails]])

    /**
     * List all Orders.
     *
     * @return All existing Orders.
     */
    def listAll(): Future[Iterable[Order]]

    /**
     * Saves a Order.
     *
     * @param Order The Order to save.
     * @return The saved Order.
     */
    def save(Order: Order): Future[Order]

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
class OrdererviceImpl @Inject() (OrderDao: OrderDao)(implicit ex: ExecutionContext) extends OrderService {

    override def find(id: Long): (Future[Option[Order]], Future[Iterable[OrderDetails]]) = OrderDao.find(id)

    override def listAll(): Future[Iterable[Order]] = OrderDao.listAll()

    override def save(Order: Order): Future[Order] = OrderDao.save(Order)

    override def update(Order: Order): Future[Order] = OrderDao.update(Order)

    override def delete(id: Long): Future[Int] = OrderDao.delete(id)
}


