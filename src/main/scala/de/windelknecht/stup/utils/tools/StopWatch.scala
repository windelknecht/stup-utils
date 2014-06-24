/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Heiko Blobner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.windelknecht.stup.utils.tools

import de.windelknecht.stup.utils.coding.Implicits._
import scala.collection.mutable

object StopWatch {
  /**
   * Ease use (java for example).
   */
  def create() = new StopWatch()
}

class StopWatch(
  now: => Double = System.currentTimeMillis().toDouble / 1000
  ) {
  // fields
  private var _started: Option[Double] = None
  private var _stopped: Option[Double] = None
  private val _rounds = new mutable.HashMap[Any, Double]()

  /**
   * Return time between 2 rounds.
   */
  def diff(
    _1stKey: Any,
    _2ndKey: Any
    ): Double = {
    val _2ndTime: Double = _rounds.getOrElse(_2ndKey, -1)
    val _1stTime: Double = _rounds.getOrElse(_1stKey, -1)

    calcDiff(_1stTime, _2ndTime)
  }

  /**
   * Return either a relative round time or an absolute.
   * Absolute means between wanted round and start time.
   * Relative means between wanted round and its direct previous round.
   *
   * If the round is not defined -1 is returned.
   */
  def diff(
    _1stKey: Any,
    getAbsolute: Boolean = false
    ): Double = if(getAbsolute) calcRoundAbsolute(_1stKey) else calcRoundRelative(_1stKey)

  /**
   * Returns calculated time between start and stop.
   * If either start or stop is not defined updating time is used.
   */
  def diffTotal: Double = calcDiff(startTime, stopTime)

  /**
   * Return a string overview over all times.
   */
  def overview: String = {
    case class StringTime(name: String, time: String, diffRelative: String = "", diffAbsolute: String = "")
    def toStringTime(r: (Any, Double)) = StringTime(r._1.toString, r._2.asHumanReadableTime, diff(r._1).asHumanReadableTime, diff(r._1, getAbsolute = true).asHumanReadableTime)

    val calculated = (
      List(StringTime(name = "start", time = startTime.asHumanReadableTime))
      ++
      _rounds
        .map(toStringTime)
        .groupBy(_.time)
        .map(r=> StringTime(r._2.map(_.name).mkString(","), r._2.head.time, r._2.head.diffRelative, r._2.head.diffAbsolute))
        .toSeq
        .sortBy(_.time)
        .toList
      ++
      List(StringTime(name = "stop", time = stopTime.asHumanReadableTime, calcDiff(_rounds.foldLeft(startTime)((i,t)=> math.max(i,t._2)), stopTime).asHumanReadableTime, diffTotal.asHumanReadableTime))
      )

    val (_1stHdr, _2ndHdr, _3rdHdr, _4thHdr) = ("name", "time", "diff (relative)", "diff (absolute)")
    val (_1stCol, _2ndCol, _3rdCol, _4thCol) = (
      calculated.foldLeft(_1stHdr.length)((i,v) => math.max(i, v.name.length)),
      calculated.foldLeft(_2ndHdr.length)((i,v) => math.max(i, v.time.length)),
      calculated.foldLeft(_3rdHdr.length)((i,v) => math.max(i, v.diffRelative.length)),
      calculated.foldLeft(_4thHdr.length)((i,v) => math.max(i, v.diffAbsolute.length))
      )
    def padLJ(s: String, len: Int) = s.padTo(len, ' ')
    def padRJ(s: String, len: Int) = padLJ(s.reverse, len).reverse
    def toRow(_1: String, _2: String, _3: String, _4: String) = s"${padLJ(_1, _1stCol)} | ${padRJ(_2, _2ndCol)} | ${padRJ(_3, _3rdCol)} | ${padRJ(_4, _4thCol)}"
    def toRowLJ(_1: String, _2: String, _3: String, _4: String) = s"${padLJ(_1, _1stCol)} | ${padLJ(_2, _2ndCol)} | ${padLJ(_3, _3rdCol)} | ${padLJ(_4, _4thCol)}"
    val hdr = toRowLJ(_1stHdr, _2ndHdr, _3rdHdr, _4thHdr)

    val sb = new mutable.StringBuilder()

    sb.append(s"$hdr\n")
    sb.append("".padTo(hdr.length, '-'))
    sb.append(s"\n")
    sb.append(calculated.map(r=> toRow(r.name, r.time, r.diffRelative, r.diffAbsolute)).mkString("\n"))

    sb.result()
  }

  /**
   * Create a round timestamp.
   */
  def round(key: Any): StopWatch = {
    if (!isRunning && !isStopped)
      start()

    if (isRunning && !_rounds.contains(key))
      _rounds += (key -> now)

    this
  }

  /**
   * Start this stop watch.
   */
  def start(): StopWatch = {
    if (!isStarted)
      _started = Some(now)

    this
  }

  /**
   * Stop this stop watch.
   */
  def stop(): StopWatch = {
    if (!isStopped)
      _stopped = Some(now)

    this
  }

  /**
   * Calc time difference between given round and start time.
   */
  private def calcRoundAbsolute(
    key: Any
    ): Double = {
    val _2ndTime: Double = _rounds.getOrElse(key, 0)
    val _1stTime = startTime

    calcDiff(_1stTime, _2ndTime)
  }

  /**
   * Calc time difference between given round and its previous round..
   */
  private def calcRoundRelative(key: Any): Double = {
    val _2ndTime: Double = _rounds.getOrElse(key, 0)
    val _1stTime = _rounds
      .filter(_._2 < _2ndTime)
      .map(_._2)
      .foldLeft(startTime)((i,t)=> math.max(i,t))

    calcDiff(_1stTime, _2ndTime)
  }

  /**
   * Calc time difference between to long values.
   */
  private def calcDiff(
    _1stTime: Double,
    _2ndTime: Double
    ): Double = if(_1stTime == -1 || _2ndTime == -1) -1l else _2ndTime - _1stTime

  /**
   * Return time value from an opt.
   * In case of None return now.
   */
  private def getTime(t: Option[Double]) = t.getOrElse(now)

  /**
   * Returns true if the stop watch is running.
   */
  private def isRunning = isStarted && !isStopped

  /**
   * Returns true if the stop watch has started.
   */
  private def isStarted = _started != None

  /**
   * Returns true if the stop watch has stopped.
   */
  private def isStopped = _stopped != None

  /**
   * Returns the start time or if not defined now.
   */
  private def startTime = getTime(_started)

  /**
   * Returns the stop time or if not defined now.
   */
  private def stopTime = getTime(_stopped)
}
