package commands

import java.sql.Timestamp

import model.StatusType.StatusType
import play.api.libs.json.Json

/**
 * Created by inakov on 12/10/14.
 */
case class UpdateStatusCommand(status: StatusType)
