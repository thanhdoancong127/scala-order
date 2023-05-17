package controllers.order

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredActionBuilder
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats.doubleFormat
import play.api.libs.json.{JsString, Json, OFormat}
import play.api.mvc._
import services.OrderService
import utils.auth.{JWTEnvironment, WithRole}
import utils.logging.RequestMarkerContext

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class OrderPostRequest(userId: Long, orderItemsRequest: Seq[OrderItemsRequest])

object OrderPostRequest {
    implicit val format: OFormat[OrderPostRequest] = Json.format[OrderPostRequest]
}

case class OrderItemsRequest(productId: Long, quantity: Int, price: Double)

object OrderItemsRequest {
    implicit val format: OFormat[OrderItemsRequest] =
        Json.format[OrderItemsRequest]
}

/**
 * Takes HTTP requests and produces JSON.
 */
class OrderController @Inject()(cc: ControllerComponents, orderService: OrderService, silhouette: Silhouette[JWTEnvironment])
                                  (implicit ec: ExecutionContext) extends AbstractController(cc) with RequestMarkerContext {

    private val logger = Logger(getClass)
    private def SecuredAction: SecuredActionBuilder[JWTEnvironment, AnyContent] = silhouette.SecuredAction

    def getById(id: Long): Action[AnyContent] = SecuredAction(WithRole[JWTAuthenticator]("Admin", "User")).async { implicit request =>
        logger.trace(s"getById: $id")
        orderService.find(id).map {
            case Some(order) => request.identity.role match {
                case "Admin" => Ok(Json.toJson(order))
                case "User" => {
                    if (order.userId == request.identity.id.get) {
                        Ok(Json.toJson(order))
                    } else {
                        Forbidden
                    }
                }
            }
            case None => NotFound
        }
    }

    def getAll: Action[AnyContent] = SecuredAction(WithRole[JWTAuthenticator]("Admin")).async { implicit request =>
        orderService.listAll().map { Order => Ok(Json.toJson(Order.map(order => order)))}
    }

    def delete(id: Long): Action[AnyContent] = SecuredAction(WithRole[JWTAuthenticator]("Admin", "User")).async { implicit request =>
        logger.trace(s"delete order with id = $id")
            request.identity.role match {
                case "Admin" =>
                    orderService.delete(id).map { deletedCnt =>
                        if (deletedCnt == 1)
                            Ok(JsString(s"delete order with id: $id successfully"))
                        else BadRequest(JsString(s"unable to delete order with $id"))
                    }
                case "User" =>
                    orderService.find(id).map {
                        case Some(order) =>
                            if (order.userId == request.identity.id.get) {
                                orderService.delete(id)
                                Ok(JsString(s"delete order with id: $id successfully"))
                            } else {
                                Forbidden
                            }
                        case None => NotFound
                    }
            }
    }

    def create: Action[AnyContent] = SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator")).async { implicit request =>
        logger.trace("create Product: ")
        request.identity.role match {
            case "Admin" => processJsonPost(None)
            case "User" => processJsonPost(request.identity.id)
            case _ => Future.successful(Forbidden)
        }
    }

    private val orderItemsMapping = mapping(
        "productId" -> longNumber,
        "quantity" -> number,
        "price" -> of(doubleFormat)
    )(OrderItemsRequest.apply)(OrderItemsRequest.unapply)

    private val formPost: Form[OrderPostRequest] = Form(
        mapping(
            "userId" -> longNumber,
            "orderItemsRequest" -> seq(orderItemsMapping)
        )(OrderPostRequest.apply)(OrderPostRequest.unapply)
    )

    private def processJsonPost[A](userId: Option[Long])(implicit request: Request[A]): Future[Result] = {

        def failure(badForm: Form[OrderPostRequest]) = {
            Future.successful(BadRequest(JsString("invalid input")))
        }

        def success(orderPostRequest: OrderPostRequest) = userId match {
            case None => orderService.save(orderPostRequest).map { order =>
                    Created(Json.toJson(order))
                }
            case Some(userId) => orderService.save(orderPostRequest.copy(userId = userId)).map { order =>
                    Created(Json.toJson(order))
                }
        }

        formPost.bindFromRequest().fold(failure, success)
    }
}
