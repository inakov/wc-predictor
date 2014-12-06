package model

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.KeyedEntity

/**
 * Created by inakov on 12/1/14.
 */

case class Floors(floors: List[Floor])
case class Floor(id: Int, rooms: List[RoomView])
case class RoomView(id: Int, name: String, currentStatus: RoomStatus)

case class Room(floor: Int, name: String) extends KeyedEntity[Int]{
  override val id: Int = 0

  def this() = this(0, "test")
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

  def insertRoom(room: Room) = inTransaction{
    roomsTable.insert(room.copy())
  }

  def loadAllFloors: Floors = {
    val rooms = findAll
    Floors(splitRooms(rooms))
  }

  def loadRoomView(room: Room): RoomView = {
    val currentRoomStatus = RoomStatus.loadCurrentStatus(room.id).getOrElse(RoomStatus.defaultStatus)
    RoomView(room.id, room.name, currentRoomStatus)
  }

  def splitRooms(rooms: List[Room]) = {
    def _partitionByFloor(floorNumber: Int, rooms: List[Room], floors: List[Floor]): List[Floor] ={

      val split = rooms.partition(room => room.floor == floorNumber)
      val floorRooms = split._1
      val roomsToSplit = split._2
      val floorView = floorRooms.map(room => loadRoomView(room))

      val updatedFloors:List[Floor] = floors:+Floor(floorNumber, floorView)

      roomsToSplit match {
        case Nil => updatedFloors
        case _ => _partitionByFloor(roomsToSplit.head.floor, roomsToSplit, updatedFloors)
      }
    }
    _partitionByFloor(rooms.head.floor, rooms, Nil)
  }

}
