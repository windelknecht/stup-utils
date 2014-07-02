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

package de.windelknecht.stup.utils.coding

import java.awt.event.{ActionEvent, ActionListener}
import java.util.UUID
import javax.swing.Timer
import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait DoItLater {
  case class WaitingJob(id: UUID, afterJob: UUID, op: () => Any)

  // fields
  private val _runningJobs = new mutable.HashMap[UUID, Future[Any]]
  private val _waitingForOtherJob = new mutable.HashSet[WaitingJob]
  private val _timedJobs = new mutable.HashMap[UUID, (Duration, Timer)]()

  /**
   * Request to run a job asynchron
   */
  protected def doIt(
    id: UUID = UUID.randomUUID()
    )(op: => Any): UUID = {
    val fut = Future {
      op
      cleanUp(id)
    }

    id.synchronized {
      _runningJobs.synchronized {
        _runningJobs += (id -> fut)
      }
    }
    id
  }

  /**
   * Request to run a job after another has finished
   */
  protected def doIt(
    id: UUID,
    afterJob: UUID
    )(op: => Any): UUID = {
    afterJob.synchronized {
      _runningJobs.get(afterJob) match {
        case Some(x) => enqueueWaitingJob(WaitingJob(id, afterJob, () => { op }))
        case None    => doIt(id)(op)
      }
    }
    id
  }

  /**
   * Create a job which is running after a timeout
   */
  protected def doIt(
    id: UUID,
    time: Duration
    )(op: => Any): UUID = {
    id.synchronized {
      _timedJobs.get(id) match {
        case Some(x) => // ignore, already running
          x._2.stop()
          val newTime = time - x._1
          _timedJobs += (id -> (newTime, createTimedJob(id, newTime, op)))

        case None    => _timedJobs += (id -> (time, createTimedJob(id, time, op)))
      }
    }
    id
  }

  /**
   * Returns true, if the job with the given id is finished
   */
  protected def isFinished(id: UUID) = {
    id.synchronized {
      _runningJobs.get(id) match {
        case Some(x) => x.isCompleted
        case None    => true
      }
    }
  }

  /**
   * Clean all references for this id
   */
  private def cleanUp(
    id: UUID
    ) {
    id.synchronized {
      _runningJobs.synchronized {
        _runningJobs -= id
      }
      _timedJobs.synchronized {
        _timedJobs -= id
      }
      runWaitJobs(id)
    }
  }

  /**
   * This method creates a timer and this timer runs the given op
   */
  private def createTimedJob(
    id: UUID,
    afterTime: Duration,
    op: => Any
    ): Timer = {
    val timer = new Timer(afterTime.toMillis.toInt, new ActionListener {
      def actionPerformed(ae: ActionEvent) {
        doIt(id)(op)
      }
    })
    timer.setRepeats(false)
    timer.start()
    timer
  }

  /**
   * Enqueue a waiting job (thread safe)
   */
  private def enqueueWaitingJob(
    job: WaitingJob
    ) {
    _waitingForOtherJob.synchronized {
      _waitingForOtherJob += job
    }
  }

  /**
   * Run all jobs waiting for given parent.
   */
  private def runWaitJobs(
    parentId: UUID
    ) {
    _waitingForOtherJob.synchronized {
      _waitingForOtherJob
        .filter(_.afterJob == parentId)
        .foreach(j=> doIt(j.id)(j.op))
    }
  }
}
