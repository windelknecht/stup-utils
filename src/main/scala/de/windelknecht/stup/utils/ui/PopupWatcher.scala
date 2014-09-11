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
  type PopupHideFunc = { def hide(): Unit; def show(arg: Any): Unit }

  // fields
  private var _activePopup: Option[PopupHideFunc] = None

  /**
   * Close currently open popup.
   */
  def close(): Unit = synchronized { hide() }

  /**
   * Activate the given popup.
   */
  def show(popup: PopupHideFunc, arg: Any): Unit = {
    synchronized {
      hide()
      _activePopup = Some(popup)
      popup.show(arg)
    }
  }

  /**
   * Hide active popup.
   *
   * THIS METHOD IS NOT REENTRANT SAFE!!!
   */
  private def hide(): Unit = {
    _activePopup match {
      case Some(x) => x.hide()
      case None =>
    }
    _activePopup = None
  }
}
