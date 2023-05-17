package controllers.user

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredActionBuilder
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.{JsString, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Request, Result}
import services.UserService
import utils.auth.{JWTEnvironment, WithRole}
import utils.logging.RequestMarkerContext
import domain.models.User

import java.text.SimpleDateFormat
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


case class UserFormInput(email: String, role: Option[String], firstName: String, lastName: String, password: Option[String], birthday: Option[String], address: Option[String], phoneNumber: Option[String])

class UserController @Inject()(cc: ControllerComponents,
                               userService: UserService,
                               passwordHasherRegistry: PasswordHasherRegistry,
                               silhouette: Silhouette[JWTEnvironment]) (implicit ec: ExecutionContext) extends AbstractController(cc) with RequestMarkerContext {

    def SecuredActionForAdmin: SecuredActionBuilder[JWTEnvironment, AnyContent] = silhouette.SecuredAction(WithRole[JWTAuthenticator]("Admin"))

    private val logger = Logger(getClass)

    private val form: Form[UserFormInput] = {
        import play.api.data.Forms._

        Form(
            mapping(
            "email" -> nonEmptyText,
            "role" -> optional(text),
            "firstName" -> nonEmptyText,
            "lastName" -> nonEmptyText,
            "password" -> optional(text(minLength = 6)),
            "birthday" -> optional(text),
            "address" -> optional(text),
            "phoneNumber" -> optional(text)
            )(UserFormInput.apply)(UserFormInput.unapply)
        )
    }

    def getById(id: Long): Action[AnyContent] = SecuredActionForAdmin.async { implicit request =>
        logger.trace(s"getById: $id")
        userService.find(id).map {
            case Some(user) => Ok(Json.toJson(UserResource.fromUser(user)))
            case None => NotFound
        }
    }

    def deleteUserById(id: Long): Action[AnyContent] = SecuredActionForAdmin.async { implicit request =>
        logger.trace(s"getById: $id")
        userService.delete(id).map { deleteCount =>
          if (deleteCount == 1) Ok(JsString(s"Delete user $id successfully"))
          else BadRequest(JsString(s"Unable to delete user $id"))
        }
    }

    def update(idParam: Long): Action[AnyContent] = SecuredActionForAdmin.async { implicit request =>
        logger.trace(s"update User id")

        // Access the user from the request
        processJsonUser(Some(idParam))
    }

    def getAll: Action[AnyContent] = SecuredActionForAdmin.async { implicit request =>
        logger.trace("getAll User")
        userService.listAll().map (users => Ok(Json.toJson(users.map(user => UserResource.fromUser(user)))))
    }

    private def processJsonUser[A](idParam: Option[Long])(implicit request: Request[A]): Future[Result] = {

        def failure(badForm: Form[UserFormInput]) = {
            Future.successful(BadRequest(JsString("Invalid Input")))
        }

        def success(input: UserFormInput) = {
            val outputFormat = new SimpleDateFormat("dd/MM/yyyy")
            val dateTime = if (input.birthday.nonEmpty) new java.sql.Date(outputFormat.parse(input.birthday.get).getTime) else null
            val authInfo = passwordHasherRegistry.current.hash(input.password.get)

            val user = User(idParam, input.email, input.role.get, input.firstName, input.lastName, Some(authInfo.password), Some(dateTime), input.address, input.phoneNumber)

            userService.update(user).map { user =>
                Created(Json.toJson(UserResource.fromUser(user)))
            }
        }

        form.bindFromRequest().fold(failure, success)
    }
}
