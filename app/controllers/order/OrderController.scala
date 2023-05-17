package controllers.order

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredActionBuilder
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import controllers.products.ProductResource
import domain.models.Product
import httpclient.ExternalServiceException
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.{JsString, Json}
import play.api.mvc._
import services.{OrderService}
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
class OrderController @Inject()(cc: ControllerComponents, orderService: OrderService, silhouette: Silhouette[JWTEnvironment])
                                  (implicit ec: ExecutionContext) extends AbstractController(cc) with RequestMarkerContext {

    private val logger = Logger(getClass)
    private def SecuredAction: SecuredActionBuilder[JWTEnvironment, AnyContent] = silhouette.SecuredAction

//    private val form: Form[OrderFormInput] = {
//        import play.api.data.Forms._
//
//        Form(
//            mapping(
//                "productName" -> nonEmptyText(minLength = 0),
//                "price" -> bigDecimal,
//                "expDate" -> optional(text),
//            )(OrderFormInput.apply)(OrderFormInput.unapply)
//        )
//    }

    def getById(id: Long): Action[AnyContent] = SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator")).async { implicit request =>
        logger.trace(s"getById: $id")
        val (orders, orderDetailList) = orderService.find(id);
        orders.map {
            case Some(order) => Ok(Json.toJson(OrderResource.fromOrder(order)))
            case None => NotFound
        }
    }

//    def getAll: Action[AnyContent] = SecuredAction(WithRole[JWTAuthenticator]("Admin")).async { implicit request =>
//        logger.trace("getAll Order")
//        orderService.listAll().map { Order =>
//            Ok(Json.toJson(Order.map(order => OrderResource.fromOrder(order))))
//        }
//    }
//
//    def delete(id: Long): Action[AnyContent] = SecuredAction(WithRole[JWTAuthenticator]("Admin", "User")).async { implicit request =>
//        logger.trace(s"Delete product: id = $id")
//        OrderService.delete(id).map { deletedCnt =>
//            if (deletedCnt == 1) Ok(JsString(s"Delete product $id successfully"))
//            else BadRequest(JsString(s"Unable to delete product $id"))
//        }
//    }
//
//    def create: Action[AnyContent] =
//        SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator")).async { implicit request =>
//            logger.trace("create Product: ")
//            processJsonProdudct(None)
//        }
//
//    def update(id: Long): Action[AnyContent] =
//        SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator")).async { implicit request =>
//            logger.trace(s"update Product id: $id")
//            processJsonProdudct(Some(id))
//        }
//
//    private def processJsonProdudct[A](id: Option[Long])(implicit request: Request[A]): Future[Result] = {
//
//        def failure(badForm: Form[ProductFormInput]) = {
//            Future.successful(BadRequest(JsString("Invalid Input")))
//        }
//
//        def success(input: ProductFormInput) = {
//            val outputFormat = new SimpleDateFormat("dd/MM/yyyy")
//            val expDate = if (input.expDate.nonEmpty) new java.sql.Date(outputFormat.parse(input.expDate.get).getTime) else (new java.sql.Date(Calendar.getInstance().getTime().getTime()))
//            // create a product from given form input
//            val product = Product(id, input.productName, input.price, expDate)
//
//            if (id.nonEmpty) {
//                OrderService.update(order).map { product =>
//                    Created(Json.toJson(OrderResource.fromOrder(order)))
//                }
//            } else {
//                OrderService.save(order).map { product =>
//                    Created(Json.toJson(OrderResource.fromOrder(order)))
//                }
//            }
//        }
//
//        form.bindFromRequest().fold(failure, success)
//    }

}
