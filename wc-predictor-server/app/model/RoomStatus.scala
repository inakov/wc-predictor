package model

import java.sql.Timestamp
import java.util.Date


import model.StatusType.StatusType
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.KeyedEntity

import play.api.libs.json._

import utils.EnumUtils

/**
 * Created by inakov on 12/2/14.
 */

object StatusType extends Enumeration {
  type StatusType = Value

  val  USABLE = Value(1, "USABLE")
  val DANGER = Value(2, "DANGER")
  val BAD = Value(3, "BAD")
  val OCCUPIED = Value(4, "OCCUPIED")

  implicit val enumReads: Reads[StatusType] = EnumUtils.enumReads(StatusType)
  implicit val enumWrites: Writes[StatusType] = EnumUtils.enumWrites
}

case class RoomStatus(roomId: Int, statusType: StatusType, creationDate: Option[Timestamp], statusExpiration: Option[Timestamp]) extends KeyedEntity[Int]{
  override val id: Int = 0

  def this() = this(0, StatusType.USABLE, None, None)
}
case class RoomStatusView(statusType: StatusType, creationDate: Option[Timestamp], statusExpiration: Option[Long])


object RoomStatus {

  val TEN_MIN = 600000
  val FIVE_MIN = 300000
  val TREE_MIN = 180000

  implicit val timestampRead = new Reads[Timestamp] {
    override def reads(json: JsValue): JsResult[Timestamp] = JsSuccess(new Timestamp(json.as[Long]))
  }
  implicit val timestampWrites = new Writes[Timestamp] {
    override def writes(o: Timestamp): JsValue = Json.toJson(o.getTime)
  }

  implicit val fmt = Json.format[RoomStatus]

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
        (roomStatus.statusExpiration.getOrElse(now) > now) and
        (roomStatus.creationDate.isNull or (roomStatus.creationDate.getOrElse(now) < now))
      )
      select(roomStatus)
      orderBy(roomStatus.creationDate desc)
    ).headOption
  }

  def insertStatus(roomStatus: RoomStatus) = inTransaction{
    roomStatusTable.insert(roomStatus)
  }

  def updateRoomStatus(roomId: Int, status: StatusType) = inTransaction{
    val now = new Timestamp(System.currentTimeMillis)
    def _updateRoomStatus(status: StatusType, creationTime: Timestamp): Unit ={
      val statusExpiration = defaultStatusExpiration(status, creationTime)

      val newStatus = RoomStatus(
        roomId, status, Option(creationTime), statusExpiration)
      insertStatus(newStatus)

      status match {
        case StatusType.DANGER => _updateRoomStatus(StatusType.BAD, statusExpiration.getOrElse(now))
        case _ =>
      }

    }
    _updateRoomStatus(status, now)
  }

  def defaultStatusExpiration(status: StatusType, creationTime: Timestamp): Option[Timestamp] = {
    status match {
      case StatusType.OCCUPIED => Option(new Timestamp(creationTime.getTime + TEN_MIN))
      case StatusType.DANGER => Option(new Timestamp(creationTime.getTime + FIVE_MIN))
      case StatusType.BAD => Option(new Timestamp(creationTime.getTime + TREE_MIN))
      case _ => None
    }
  }

  def createStatusView(roomStatus: RoomStatus): RoomStatusView ={
    RoomStatusView(roomStatus.statusType, roomStatus.creationDate, calculateExpiration(roomStatus.statusExpiration))
  }

  def calculateExpiration(statusExpiration: Option[Timestamp]): Option[Long] ={
    val now = new Timestamp(System.currentTimeMillis)
    val expiry = statusExpiration.getOrElse(now)
    val timeToExpiry = (expiry.getTime - now.getTime) / 1000

    if( timeToExpiry > 0) Option(timeToExpiry)
    else None
  }
}