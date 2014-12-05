package model

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.KeyedEntity

/**
 * Created by inakov on 12/1/14.
 */

case class Floors(floors: List[Floor])
case class Floor(id: Int, rooms: List[Room])
case class RoomView(id: Int, name: String, currentStatus: RoomStatus)

case class Room(floor: Int, name: String) extends KeyedEntity[Int]{
  override val id: Int = 0
}



object Room {
  import Database.{roomsTable}

  val allRoomsQuery = from(roomsTable)( room =>
    select(room)
  )

  def findAll = inTransaction{
    allRoomsQuery.toList
  }

  def findById(id: Int) = inTransaction{
    from(roomsTable)( room =>
      where(room.id === id)
      select(room)
    ).headOption
  }

  def loadAllFloors: Floors = {
    val rooms = findAll
    val roomsViews = rooms.map { room =>
      val currentRoomStatus = RoomStatus.loadCurrentStatus(room.id).getOrElse(RoomStatus.defaultStatus)
      RoomView(room.id, room.name, currentRoomStatus)
    }
    val splitedRooms =

  }

  def splitRooms(rooms: List[Room]) = {
    def _partitionByFloor(floorNumber: Int, rooms: List[Room], floors: List[Floor]): List[Floor] ={
      rooms match {
        case Nil => floors
        case _ =>
      }
      val split = rooms.partition(room => room.floor == floorNumber)
      val floorRooms = split._1

      Floor(floorNumber,floorRooms)
    }
  }

}
