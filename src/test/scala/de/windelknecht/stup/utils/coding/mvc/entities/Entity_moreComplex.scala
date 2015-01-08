package de.windelknecht.stup.utils.coding.mvc.entities

import java.util.UUID

import de.windelknecht.stup.utils.coding.mvc.Entity

case class Entity_moreComplex(
  id: String = UUID.randomUUID().toString,
  v01: Byte,
//  v02: java.lang.Byte,
  v03: Char,
//  v04: java.lang.Character,
  v05: Double,
//  v06: java.lang.Double,
  v07: Float,
//  v08: java.lang.Float,
  v09: Int,
//  v10: java.lang.Integer,
  v11: String,
  v12: java.lang.String
  ) extends Entity
