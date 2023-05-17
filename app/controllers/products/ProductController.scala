package controllers.products

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredActionBuilder
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import domain.models.Product
import httpclient.ExternalServiceException
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.{JsString, Json}
import play.api.mvc._
import services.{ExternalProductService, ProductService}
import utils.auth.{JWTEnvironment, WithRole}
import utils.logging.RequestMarkerContext

import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

case class ProductFormInput(productName: String, price: BigDecimal, expDate: Option[String])

/**
 * Takes HTTP requests and produces JSON.
 */
class ProductsController @Inject()(cc: ControllerComponents, productService: ProductService, extProductService: ExternalProductService, silhouette: Silhouette[JWTEnvironment])
                                  (implicit ec: ExecutionContext) extends AbstractController(cc) with RequestMarkerContext {

    private val logger = Logger(getClass)
    private val form: Form[ProductFormInput] = {
        import play.api.data.Forms._

        Form(
            mapping(
                "productName" -> nonEmptyText(minLength = 0),
                "price" -> bigDecimal,
                "expDate" -> optional(text),
            )(ProductFormInput.apply)(ProductFormInput.unapply)
        )
    }

    def getById(id: Long): Action[AnyContent] = SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator")).async { implicit request =>
        logger.trace(s"getById: $id")
        productService.find(id).map {
            case Some(product) => Ok(Json.toJson(ProductResource.fromProduct(product)))
            case None => NotFound
        }
    }

    def getAll: Action[AnyContent] =
        SecuredAction(WithRole[JWTAuthenticator]("User", "Admin", "Operator")).async { implicit request =>
            logger.trace("getAll Products")
            productService.listAll().map { products =>
                Ok(Json.toJson(products.map(product => ProductResource.fromProduct(product))))
            }
        }

    def SecuredAction: SecuredActionBuilder[JWTEnvironment, AnyContent] = silhouette.SecuredAction

    def create: Action[AnyContent] =
        SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator")).async { implicit request =>
            logger.trace("create Product: ")
            processJsonProdudct(None)
        }

    private def processJsonProdudct[A](id: Option[Long])(implicit request: Request[A]): Future[Result] = {

        def failure(badForm: Form[ProductFormInput]) = {
            Future.successful(BadRequest(JsString("Invalid Input")))
        }

        def success(input: ProductFormInput) = {
            val outputFormat = new SimpleDateFormat("dd/MM/yyyy")
            val expDate = if (input.expDate.nonEmpty) new java.sql.Date(outputFormat.parse(input.expDate.get).getTime) else (new java.sql.Date(Calendar.getInstance().getTime().getTime()))
            // create a product from given form input
            val product = Product(id, input.productName, input.price, expDate)

            if(id.nonEmpty) {
                productService.update(product).map { product =>
                    Created(Json.toJson(ProductResource.fromProduct(product)))
                }
            } else {
                productService.save(product).map { product =>
                    Created(Json.toJson(ProductResource.fromProduct(product)))
                }
            }
        }

        form.bindFromRequest().fold(failure, success)
    }

    def update(id: Long): Action[AnyContent] =
        SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator")).async { implicit request =>
            logger.trace(s"update Product id: $id")
            processJsonProdudct(Some(id))
        }

    def delete(id: Long): Action[AnyContent] = SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator")).async { implicit request =>
        logger.trace(s"Delete product: id = $id")
        productService.delete(id).map { deletedCnt =>
            if (deletedCnt == 1) Ok(JsString(s"Delete product $id successfully"))
            else BadRequest(JsString(s"Unable to delete product $id"))
        }
    }

    def getAllExternal: Action[AnyContent] = SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator")).async { implicit request =>
        logger.trace("getAll External Products")

        // try/catch Future exception with transform
        extProductService.listAll().transform {
            case Failure(exception) => handleExternalError(exception)
            case Success(products) => Try(Ok(Json.toJson(products.map(product => ProductResource.fromProduct(product)))))
        }
    }

    private def handleExternalError(throwable: Throwable): Try[Result] = {
        throwable match {
            case ese: ExternalServiceException =>
                logger.trace(s"An ExternalServiceException occurred: ${ese.getMessage}")
                if (ese.error.isEmpty)
                    Try(BadRequest(JsString(s"An ExternalServiceException occurred. statusCode: ${ese.statusCode}")))
                else Try(BadRequest(Json.toJson(ese.error.get)))
            case _ =>
                logger.trace(s"An other exception occurred on getAllExternal: ${throwable.getMessage}")
                Try(BadRequest(JsString("Unable to create an external Products")))
        }
    }

    def createExternal: Action[AnyContent] = SecuredAction(WithRole[JWTAuthenticator]("Creator")).async { implicit request =>
        logger.trace("create External Product: ")

        def failure(badForm: Form[ProductFormInput]) = {
            Future.successful(BadRequest(JsString("Invalid Input")))
        }

        def success(input: ProductFormInput) = {
            val outputFormat = new SimpleDateFormat("dd/MM/yyyy")
            val expDate = if (input.expDate.nonEmpty) new java.sql.Date(outputFormat.parse(input.expDate.get).getTime) else (new java.sql.Date(Calendar.getInstance().getTime().getTime()))
            // create a Product from given form input
            val product = Product(Some(999L), input.productName, input.price, expDate)

            extProductService.save(product).transform {
                case Failure(exception) => handleExternalError(exception)
                case Success(product) => Try(Created(Json.toJson(ProductResource.fromProduct(product))))
            }
        }

        form.bindFromRequest().fold(failure, success)
    }
}
