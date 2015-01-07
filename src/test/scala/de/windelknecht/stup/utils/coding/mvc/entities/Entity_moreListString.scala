package de.windelknecht.stup.utils.coding.mvc.entities

import java.util.UUID

import de.windelknecht.stup.utils.coding.mvc.Entity

case class Entity_moreListString(
  id: String = UUID.randomUUID().toString,
  list: List[String]
  ) extends Entity
