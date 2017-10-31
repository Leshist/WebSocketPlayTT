package tables

import play.api.libs.json._

class TableStorage {
  private var myTables: JsObject = defaultTables
  private def defaultTables: JsObject = Json.obj(
    "tables" -> Json.arr(
      Json.obj(
        "id"           -> 1,
        "name"         -> "table - James Bond",
        "participants" -> 7
      ),
      Json.obj(
        "id"           -> 2,
        "name"         -> "table - Mission Impossible",
        "participants" -> 4
      )
    )
  )

  def getTables: JsValue = myTables

  def addTableAndReturnMsg(msg: JsValue): JsValue = {
    val afterId              = (msg             \ "after_id").as[Int]
    val tableToAdd           = (msg             \ "table").as[JsObject]
    val nameToAdd            = (tableToAdd      \ "name").as[String]
    val participantsToAdd    = (tableToAdd      \ "participants").as[Int]

    //                               (id,  name,   participants)
    val existingTablesContents: List[(Int, String, Int)] = getExistingTablesContents
    val currentMaxId = existingTablesContents.maxBy(_._1)._1

    afterId match {
      case -1 =>
        val oldTables = existingTablesContents map {
          entry     => Json.obj("id"           -> (entry._1 + 1),
                                "name"         -> entry._2,
                                "participants" -> entry._3)
        }
        val newTable = Json.obj("id"           -> 1,
                                "name"         -> nameToAdd,
                                "participants" -> participantsToAdd)

        mutateMyTables(Json.obj("tables" -> (oldTables :+ newTable)))
        composeAddTableResponse(1, afterId, nameToAdd, participantsToAdd)

      case _  =>
        val oldTables = existingTablesContents map {
          entry       => Json.obj("id"           -> entry._1,
                                  "name"         -> entry._2,
                                  "participants" -> entry._3)
        }

        val newTable =   Json.obj("id"           -> (currentMaxId + 1),
                                  "name"         -> nameToAdd,
                                  "participants" -> participantsToAdd)

                       // since size is small its ok i guess, but O(n) here
        mutateMyTables(Json.obj("tables" -> (oldTables :+ newTable)))
        composeAddTableResponse(currentMaxId + 1, afterId, nameToAdd, participantsToAdd)
    }
  }

  def updateTableAndReturnMsg(msg: JsValue): JsValue = {
    val tableToUpdate        = (msg             \ "table").as[JsObject]
    val idOfObjectToUpdate   = (tableToUpdate   \ "id").as[Int]
    val nameToSet            = (tableToUpdate   \ "name").as[String]
    val participantsToSet    = (tableToUpdate   \ "participants").as[Int]

    val existingTablesContents: List[(Int, String, Int)] = getExistingTablesContents

    val existingObject = existingTablesContents.find(obj => obj._1 == idOfObjectToUpdate)
    existingObject match {
      case Some(objectToUpdate) =>
        val updated = existingTablesContents map {
          case `objectToUpdate`         => Json.obj(
            "id"                      -> idOfObjectToUpdate,
            "name"                    -> nameToSet,
            "participants"            -> participantsToSet
          )
          case objectToLeaveUntouched   => Json.obj(
            "id"                      -> objectToLeaveUntouched._1,
            "name"                    -> objectToLeaveUntouched._2,
            "participants"            -> objectToLeaveUntouched._3)
        }

        mutateMyTables(Json.obj("tables" -> updated))
        composeUpdateTableResponse(idOfObjectToUpdate, nameToSet, participantsToSet)

      case None => Json.obj("$type" -> "update failed",
                            "id" -> idOfObjectToUpdate)
    }
  }


  def removeTableAndReturnMsg(msg: JsValue): JsValue = {
    val idToRemove = (msg \ "id").as[Int]
    val existingTablesContents: List[(Int, String, Int)] = getExistingTablesContents
    val existingObject = existingTablesContents.find(obj => obj._1 == idToRemove)

    existingObject match {
      case Some(objectToRemove) =>
        val objectIndex = existingTablesContents.indexOf(objectToRemove)
        val withoutRemoved = existingTablesContents.zipWithIndex.filter(_._2 != objectIndex).map(_._1)
        val updated = withoutRemoved map ( entry => Json.obj(
            "id"                      -> entry._1,
            "name"                    -> entry._2,
            "participants"            -> entry._3)
        )
        mutateMyTables(Json.obj("tables" -> updated))
        composeRemoveTableResponse(idToRemove)

      case None => Json.obj("$type" -> "removal_failed",
                            "id"    -> idToRemove)
    }
  }


  private def getExistingTablesContents: List[(Int, String, Int)] = {
    val existingTables       = (myTables        \ "tables").get
    val existingIds          = (existingTables \\ "id"           map(_.as[Int])).toList
    val existingNames        = (existingTables \\ "name"         map(_.as[String])).toList
    val existingParticipants = (existingTables \\ "participants" map(_.as[Int])).toList

    existingIds zip existingNames zip existingParticipants map {
      case ((x, y), z) => (x,y,z)
    }

  }
  private def mutateMyTables(o: JsObject): Unit ={
    myTables = myTables ++ o
  }

  private def composeAddTableResponse(id: Int, afterId: Int, name: String, participants: Int): JsValue = {
    Json.obj(
      "$type"    -> "table_added",
      "after_id" ->  afterId,
      "table"    -> Json.obj(
        "id"           -> id,
        "name"         -> name,
        "participants" -> participants)
    )
  }

  private def composeUpdateTableResponse(id: Int, name: String, participants: Int): JsValue = {
    Json.obj(
      "$type"    -> "table_updated",
      "table"    -> Json.obj(
        "id"           -> id,
        "name"         -> name,
        "participants" -> participants)
    )
  }

  private def composeRemoveTableResponse(id: Int): JsValue = Json.obj("$type" -> "table_removed", "id" -> id)

}
