package controllers

import repo.CarRepository
import com.google.inject.Inject
import models.Car
import play.api.Logger
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json._
import play.api.libs.json.{ JsError, JsValue, Json }
import play.api.mvc._
import repo.EmployeeRepository
import utils.Constants
import utils.JsonFormat._

import scala.concurrent.Future

/**
 * 	amit.dixit
 */
class CarController @Inject() (empRepository: CarRepository, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  import Constants._

  val logger = Logger(this.getClass())

  /**
   * Handles request for getting all car from the database
   */
  def list() = Action.async {
    empRepository.getAll().map { res =>
      logger.info("Car list: " + res)
      Ok(successResponse(Json.toJson(res), Messages("car.success.carList")))
    }
  }

  /**
   * Handles request for creation of new car
   */
  def create() = Action.async(parse.json) { request =>
    logger.info("Car Json ===> " + request.body)
    request.body.validate[Car].fold(error => Future.successful(BadRequest(JsError.toJson(error))), { emp =>
      empRepository.insert(emp).map { createdCarId =>
        Ok(successResponse(Json.toJson(Map("id" -> createdCarId)), Messages("car.success.created")))
      }
    })
  }

  /**
   * Handles request for deletion of existing car by car_id
   */
  def delete(carId: Int) = Action.async { request =>
    empRepository.delete(carId).map { _ =>
      Ok(successResponse(Json.toJson("{}"), Messages("car.success.deleted")))
    }
  }

  /**
   * Handles request for get car details for editing
   */
  def edit(carId: Int): Action[AnyContent] = Action.async { request =>
    empRepository.getById(carId).map { empOpt =>
      empOpt.fold(Ok(errorResponse(Json.toJson("{}"), Messages("car.error.carNotExist"))))(car => Ok(
        successResponse(Json.toJson(car), Messages("car.success.car"))))
    }
  }

  private def errorResponse(data: JsValue, message: String) = {
    obj("status" -> ERROR, "data" -> data, "msg" -> message)
  }

  /**
   * Handles request for update existing car
   */
  def update = Action.async(parse.json) { request =>
    logger.info("Car Json ===> " + request.body)
    request.body.validate[Car].fold(error => Future.successful(BadRequest(JsError.toJson(error))), { car =>
      empRepository.update(car).map { res => Ok(successResponse(Json.toJson("{}"), Messages("car.success.updated"))) }
    })
  }

  private def successResponse(data: JsValue, message: String) = {
    obj("status" -> SUCCESS, "data" -> data, "msg" -> message)
  }
}