package model

/**
 * Created by inakov on 12/1/14.
 */
case class Room(
                 id: Int, number: Int, floor: Int, status: String)

object Room {

  var rooms = Set(Room(1, 1, 5, "AVAILABLE"), Room(2, 2, 5, "AVAILABLE"))

  def findAll = rooms.toList.sortBy(_.id)

  def findById(id: Int) = rooms.find(_.id == id)
}
