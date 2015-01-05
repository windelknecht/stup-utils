package de.windelknecht.stup.utils.coding.mvc.entities

import java.util.UUID

import de.windelknecht.stup.utils.coding.mvc.Entity

case class Entity_moreSimple(
  id: UUID = UUID.randomUUID(),
  name: String = "sohi",
  age: Int = 32
  ) extends Entity
