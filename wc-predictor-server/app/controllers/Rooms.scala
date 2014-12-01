package controllers

import model.Room
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}

/**
 * Created by inakov on 12/1/14.
 */
object Rooms extends Controller{
  implicit val roomsWrites = Json.writes[Room]
  implicit val roomsReader = Json.reads[Room]

  def list = Action {
    val rooms = Room.findAll
    Ok(Json.toJson(rooms))
  }

  def show(id: Int) = Action{ implicit request =>
    Room.findById(id).map { room =>
      Ok(Json.toJson(room))
    }.getOrElse(BadRequest("Bad request"))
  }

  def update = Action(parse.json){ implicit request =>
    request.body.validate[Room].map{
      room => Ok(Json.toJson(room))
    }.recoverTotal{
      e => BadRequest("Detected error:"+ JsError.toFlatJson(e))
    }
  }
}
