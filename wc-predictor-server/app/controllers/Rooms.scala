package controllers

import java.sql.Timestamp

import model._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

/**
 * Created by inakov on 12/1/14.
 */
object Rooms extends Controller{
  implicit val roomsWrites = Json.writes[Room]
  implicit val roomsReader = Json.reads[Room]

  implicit val timestampRead = new Reads[Timestamp] {
    override def reads(json: JsValue): JsResult[Timestamp] = JsSuccess(new Timestamp(json.as[Long]))
  }
  implicit val timestampWrites = new Writes[Timestamp] {
    override def writes(o: Timestamp): JsValue = Json.toJson(o.getTime)
  }
  implicit val statusViewWrites = Json.writes[RoomStatusView]
  implicit val statusViewReader = Json.reads[RoomStatusView]

  implicit val roomViewWrites = Json.writes[RoomView]
  implicit val roomViewReader = Json.reads[RoomView]

  implicit val floorWrites = Json.writes[Floor]
  implicit val floorReader = Json.reads[Floor]

  implicit val floorsWrites = Json.writes[Floors]
  implicit val floorsReader = Json.reads[Floors]

  def listFloors = Action {
    val floors = Room.loadAllFloors
    Ok(Json.toJson(floors))
  }

  def show(id: Int) = Action{ implicit request =>
    Room.findById(id).map { room =>
      Ok(Json.toJson(Room.loadRoomView(room)))
    }.getOrElse(BadRequest("Bad request"))
  }
}
