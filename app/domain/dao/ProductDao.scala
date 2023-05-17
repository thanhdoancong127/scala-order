package domain.dao

import com.google.inject.{ImplementedBy, Inject, Singleton}
import domain.models.Product
import domain.tables.ProductTable
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

/**
 * A pure non-blocking interface for accessing Products table
 */
@ImplementedBy(classOf[ProductDaoImpl])
trait ProductDao {

  /**
   * Finds a Product by id.
   *
   * @param id The Product id to find.
   * @return The found Product or None if no Product for the given id could be found.
   */
  def find(id: Long): Future[Option[Product]]

  /**
   * List all Products.
   *
   * @return All existing Products.
   */
  def listAll(): Future[Iterable[Product]]

  /**
   * Saves a Product.
   *
   * @param Product The Product to save.
   * @return The saved Product.
   */
  def save(Product: Product): Future[Product]

  /**
   * Saves all Products.
   *
   * @param list The Products to save.
   * @return The saved Products.
   */
  def saveAll(list: Seq[Product]): Future[Seq[Product]]

  /**
   * Updates a Product.
   *
   * @param Product The Product to update.
   * @return The saved Product.
   */
  def update(Product: Product): Future[Product]

  /**
   * Deletes a Product
   * @param id The Product's id to delete.
   * @return The deleted Product.
   */
  def delete(id: Long): Future[Int]
}

/**
 * ProductDao implementation class
 *
 * @param daoRunner DaoRunner for running query in a transaction
 * @param ec Execution context
 */
@Singleton
class ProductDaoImpl @Inject()(daoRunner: DaoRunner)(implicit ec: DbExecutionContext) extends ProductDao {

    private val Products = TableQuery[ProductTable]

    override def find(id: Long): Future[Option[Product]] = daoRunner.run {
        Products.filter(_.id === id).result.headOption
    }

    override def listAll(): Future[Iterable[Product]] = daoRunner.run {
        Products.result
    }

    override def save(Product: Product): Future[Product] = daoRunner.run {
        Products returning Products += Product
    }

    override def saveAll(list: Seq[Product]): Future[Seq[Product]] = daoRunner.run {
        (Products ++= list).map(_ => list)
    }

    override def update(Product: Product): Future[Product] = daoRunner.run {
        Products.filter(_.id === Product.id).update(Product).map(_ => Product)
    }

    override def delete(id: Long): Future[Int] = daoRunner.run {
        Products.filter(_.id === id).delete
    }
}
