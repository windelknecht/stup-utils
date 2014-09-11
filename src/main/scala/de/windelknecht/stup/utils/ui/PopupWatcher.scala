package de.windelknecht.stup.utils.ui

/**
 * Created by me.
 * User: Heiko Blobner
 * Mail: heiko.blobner@gmx.de
 *
 * Date: 11.09.14
 * Time: 21:41
 */
object PopupWatcher {
  // fields
  private var _activePopup: Option[() => Unit] = None

  /**
   * Close currently open popup.
   */
  def close(): Unit = synchronized { hide() }

  /**
   * Activate the given popup.
   */
  def show(showFn: () => Unit, hideFn: () => Unit): Unit = {
    synchronized {
      hide()
      _activePopup = Some(hideFn)
      showFn()
    }
  }

  /**
   * Hide active popup.
   *
   * THIS METHOD IS NOT REENTRANT SAFE!!!
   */
  private def hide(): Unit = {
    _activePopup match {
      case Some(x) => x()
      case None =>
    }
    _activePopup = None
  }
}
