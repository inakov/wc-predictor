package model

import java.sql.Timestamp
import java.util.Date

import model.StatusType.StatusType

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.KeyedEntity

import play.api.libs.json._
import play.api.libs.functional.syntax._

import utils.EnumUtils

/**
 * Created by inakov on 12/2/14.
 */

object StatusType extends Enumeration {
  type StatusType = Value

  val  USABLE = Value(1, "USABLE")
  val DANGER = Value(2, "DANGER")
  val UNCOOL = Value(3, "UNCOOL")
  val OCCUPIED = Value(4, "OCCUPIED")

  implicit val enumReads: Reads[StatusType] = EnumUtils.enumReads(StatusType)
  implicit val enumWrites: Writes[StatusType] = EnumUtils.enumWrites
}

case class RoomStatus(roomId: Int, statusType: StatusType, creationDate: Option[Timestamp], statusExpiration: Option[Timestamp]) extends KeyedEntity[Int]{
  override val id: Int = 0

  def this() = this(0, StatusType.USABLE, None, None)
}

object RoomStatus {

  implicit val fmt = Json.format[RoomStatus]

  implicit val rds: Reads[Timestamp] = (__ \ "time").read[Long].map{ long => new Timestamp(long) }
  implicit val wrs: Writes[Timestamp] = (__ \ "time").write[Long].contramap{ (a: Timestamp) => a.getTime }
  implicit val timestampFmt: Format[Timestamp] = Format(rds, wrs)

  def fromJson(json: JsValue) = Json.fromJson[RoomStatus](json).get
  def toJson(enumCaseClass: RoomStatus) = Json.toJson(enumCaseClass)

  import Database.{roomStatusTable}

  def defaultStatus = RoomStatus(0, StatusType.USABLE, None, None)

  def findByRoomId(roomId: Int) = inTransaction{
    from(roomStatusTable)( roomStatus =>
      where(roomStatus.roomId === roomId)
      select(roomStatus)
    ).toList
  }

  def loadCurrentStatus(roomId: Int) = inTransaction{
    val now = new Timestamp(System.currentTimeMillis())
    from(roomStatusTable)(roomStatus =>
      where(
        (roomStatus.roomId === roomId) and
        (roomStatus.statusExpiration.getOrElse(now) < now)
      )
      select(roomStatus)
    ).headOption
  }

}