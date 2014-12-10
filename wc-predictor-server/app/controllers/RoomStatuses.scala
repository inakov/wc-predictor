package controllers

import java.sql.Timestamp

import commands.UpdateStatusCommand
import controllers.Rooms._
import model.{Room, RoomStatus}
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
/**
 * Created by inakov on 12/10/14.
 */
object RoomStatuses extends Controller {
  implicit val timestampRead = new Reads[Timestamp] {
    override def reads(json: JsValue): JsResult[Timestamp] = JsSuccess(new Timestamp(json.as[Long]))
  }
  implicit val timestampWrites = new Writes[Timestamp] {
    override def writes(o: Timestamp): JsValue = Json.toJson(o.getTime)
  }
  implicit val updateStatusCommandWrites = Json.writes[UpdateStatusCommand]
  implicit val updateStatusCommandReader = Json.reads[UpdateStatusCommand]

  def update(roomId: Int) = Action(parse.json) { implicit request =>
    request.body.validate[UpdateStatusCommand].map { updateCommand => {
      RoomStatus.updateRoomStatus(roomId, updateCommand.status)

      Room.findById(roomId).map { room =>
        Ok(Json.toJson(Room.loadRoomView(room)))
      }.getOrElse(BadRequest("Bad request"))
    }
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }
}
