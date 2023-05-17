package domain.dao

import com.google.inject.{ImplementedBy, Inject, Singleton}
import domain.models.{Order, OrderDetails}
import domain.tables.{OrderDetailsTable, OrderTable}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{Await, Future}

/**
 * A pure non-blocking interface for accessing Orders table
 */
@ImplementedBy(classOf[OrderDaoImpl])
trait OrderDao {

  /**
   * Finds a Order by id.
   *
   * @param id The Order id to find.
   * @return The found Order or None if no Order for the given id could be found.
   */
  def find(id: Long): Future[Option[Order]]

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
   * Saves all Orders.
   *
   * @param list The Orders to save.
   * @return The saved Orders.
   */
  def saveAll(list: Seq[Order]): Future[Seq[Order]]

  /**
   * Updates a Order.
   *
   * @param Order The Order to update.
   * @return The saved Order.
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
 * OrderDao implementation class
 *
 * @param daoRunner DaoRunner for running query in a transaction
 * @param ec Execution context
 */
@Singleton
class OrderDaoImpl @Inject()(daoRunner: DaoRunner)(implicit ec: DbExecutionContext) extends OrderDao {

    private val orders = TableQuery[OrderTable]

    override def find(id: Long): Future[Option[Order]] = daoRunner.run {orders.filter(_.id === id).result.headOption}

    override def listAll(): Future[Iterable[Order]] = daoRunner.run {
        orders.result
    }

    override def save(Order: Order): Future[Order] = daoRunner.run {
        orders returning orders += Order
    }

    override def saveAll(list: Seq[Order]): Future[Seq[Order]] = daoRunner.run {
        (orders ++= list).map(_ => list)
    }

    override def update(Order: Order): Future[Order] = daoRunner.run {
        orders.filter(_.id === Order.id).update(Order).map(_ => Order)
    }

    override def delete(id: Long): Future[Int] = daoRunner.run {
        orders.filter(_.id === id).delete
    }
}
