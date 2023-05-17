package services

import com.google.inject.{ImplementedBy, Inject, Singleton}
import domain.models.Product
import httpclient.ExternalPostClient
import play.api.Configuration
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}

/**
 * ExternalPostService.
 */
@ImplementedBy(classOf[ExternalProductServiceImpl])
trait ExternalProductService {

  /**
   * List all External products.
   *
   * @return All external posts.
   */
  def listAll(): Future[Iterable[Product]]

  /**
   * Saves an external products.
   *
   * @param product The external post to save.
   * @return The saved post.
   */
  def save(product: Product): Future[Product]

}

/**
 * Handles actions to External products.
 *
 * @param client  The HTTP Client instance
 * @param ex      The execution context.
 */
@Singleton
class ExternalProductServiceImpl @Inject()(client: ExternalPostClient, config: Configuration)
                                       (implicit ex: ExecutionContext) extends ExternalProductService {

  def getAllPosts: String = config.get[String]("external.products.getAllPosts")

  def createPost: String = config.get[String]("external.products.createPost")

  override def listAll(): Future[Iterable[Product]] = client.get[Seq[Product]](getAllPosts)

  override def save(post: Product): Future[Product] = client.post[Product](createPost, Some(Json.toJson(post)))

}


