package model

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema

/**
 * Created by inakov on 12/5/14.
 */
object Database extends Schema{

  val roomsTable = table[Room]("room")
  val roomStatusTable = table[RoomStatus]("room_status")

  on(this.roomsTable){ room =>
    declare(
      room.id is(autoIncremented)
    )
  }

  on(this.roomStatusTable){ roomStatus =>
    declare(
    roomStatus.id is(autoIncremented)
    )
  }

}
