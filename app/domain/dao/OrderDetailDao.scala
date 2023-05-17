package domain.dao

import com.google.inject.{ImplementedBy, Inject, Singleton}
import domain.models.{OrderDetails}
import domain.tables.{OrderDetailsTable}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

@ImplementedBy(classOf[OrderDetailDaoImpl])
trait OrderDetailDao {

    /**
     * Finds an order detail by id.
     *
     * @param id The order detail's id to find.
     * @return The found order detail or None if no order detail for the given id could be found.
     */
    def find(id: Long): Future[Option[OrderDetails]]

    /**
     * Finds an order detail by orderId.
     *
     * @param orderId The order's id of order detail to find.
     * @return The found order detail or None if no order detail for the given id could be found.
     */
    def findByOrderId(orderId: Long): Future[Option[OrderDetails]]

    /**
     * Finds all order details by orderId.
     *
     * @param orderId The order's id of order detail to find.
     * @return All the found order details for the given orderId could be found.
     */
    def findAllByOrderId(orderId: Long): Future[Seq[OrderDetails]]

    /**
     * List all OrderDetails.
     *
     * @return All existing order details.
     */
    def listAll(): Future[Iterable[OrderDetails]]

    /**
     * Saves an order detail.
     *
     * @param orderDetail The order detail to save.
     * @return The saved order detail.
     */
    def save(orderDetail: OrderDetails): Future[OrderDetails]

    /**
     * Saves all order details.
     *
     * @param list The order details to save.
     * @return The saved order details.
     */
    def saveAll(list: Seq[OrderDetails]): Future[Seq[OrderDetails]]

    /**
     * Updates an order detail.
     *
     * @param orderDetail The order detail to update.
     * @return The saved order detail.
     */
    def update(orderDetail: OrderDetails): Future[OrderDetails]

    /**
     * Deletes an order detail
     *
     * @param id The order detail's id to delete.
     * @return The deleted order detail's id.
     */
    def delete(id: Long): Future[Int]
}

@Singleton
class OrderDetailDaoImpl @Inject()(daoRunner: DaoRunner)(
    implicit ec: DbExecutionContext
) extends OrderDetailDao {

    private val orderDetails = TableQuery[OrderDetailsTable]

    override def find(id: Long): Future[Option[OrderDetails]] = daoRunner.run {
        orderDetails.filter(_.id === id).result.headOption
    }

    override def findByOrderId(orderId: Long): Future[Option[OrderDetails]] =
        daoRunner.run {
            orderDetails.filter(_.orderId === orderId).result.headOption
        }

    override def findAllByOrderId(orderId: Long): Future[Seq[OrderDetails]] =
        daoRunner.run {
            orderDetails.filter(_.orderId === orderId).result
        }

    override def listAll(): Future[Iterable[OrderDetails]] = daoRunner.run {
        orderDetails.result
    }

    override def save(orderDetail: OrderDetails): Future[OrderDetails] =
        daoRunner.run {
            orderDetails returning orderDetails += orderDetail
        }

    override def saveAll(list: Seq[OrderDetails]): Future[Seq[OrderDetails]] =
        daoRunner.run {
            (orderDetails ++= list).map(_ => list)
        }

    override def update(orderDetail: OrderDetails): Future[OrderDetails] =
        daoRunner.run {
            orderDetails
                .filter(_.id === orderDetail.id)
                .update(orderDetail)
                .map(_ => orderDetail)
        }

    override def delete(id: Long): Future[Int] = daoRunner.run {
        orderDetails.filter(_.id === id).delete
    }
}