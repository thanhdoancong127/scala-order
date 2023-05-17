package services

import com.google.inject.{ImplementedBy, Inject, Singleton}
import domain.dao.ProductDao
import domain.models.Product

import scala.concurrent.{ExecutionContext, Future}

/**
 * ProductService.
 */
@ImplementedBy(classOf[ProducterviceImpl])
trait ProductService{

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
     * Updates a Product.
     *
     * @param Product The Product to update.
     * @return The updated Product.
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
 * Handles actions to Products.
 *
 * @param ProductDao The Product DAO implementation.
 * @param ex      The execution context.
 */
@Singleton
class ProducterviceImpl @Inject() (ProductDao: ProductDao)(implicit ex: ExecutionContext) extends ProductService {

    override def find(id: Long): Future[Option[Product]] = ProductDao.find(id)

    override def listAll(): Future[Iterable[Product]] = ProductDao.listAll()

    override def save(Product: Product): Future[Product] = ProductDao.save(Product)

    override def update(Product: Product): Future[Product] = ProductDao.update(Product)

    override def delete(id: Long): Future[Int] = ProductDao.delete(id)
}


