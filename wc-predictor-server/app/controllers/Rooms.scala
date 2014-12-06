package controllers

import model.{RoomView, Floor, Floors, Room}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}

/**
 * Created by inakov on 12/1/14.
 */
object Rooms extends Controller{
  implicit val roomsWrites = Json.writes[Room]
  implicit val roomsReader = Json.reads[Room]

  implicit val roomViewWrites = Json.writes[RoomView]
  implicit val roomViewReader = Json.reads[RoomView]

  implicit val floorWrites = Json.writes[Floor]
  implicit val floorReader = Json.reads[Floor]

  implicit val floorsWrites = Json.writes[Floors]
  implicit val floorsReader = Json.reads[Floors]

  def list = Action {
    val rooms = Room.findAll
    Ok(Json.toJson(rooms))
  }

  def listFloors = Action {
    val floors = Room.loadAllFloors
    Ok(Json.toJson(floors))
  }

  def show(id: Int) = Action{ implicit request =>
    Room.findById(id).map { room =>
      Ok(Json.toJson(room))
    }.getOrElse(BadRequest("Bad request"))
  }

  /*def update = Action(parse.json){ implicit request =>
    request.body.validate[Room].map{ room =>{
      Room.insertRoom(room)
      Ok(Json.toJson(room))
     }
    }.recoverTotal{
      e => BadRequest("Detected error:"+ JsError.toFlatJson(e))
    }
  }*/
}
