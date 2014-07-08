/**
 * Code from http://letitcrash.com/post/30165507578/shutdown-patterns-in-akka-2
 *
 * It is also published on gist.github.com.
 */
class ProductionReaper extends Reaper {
  // Shutdown
  def allSoulsReaped(): Unit = context.system.shutdown()
}
