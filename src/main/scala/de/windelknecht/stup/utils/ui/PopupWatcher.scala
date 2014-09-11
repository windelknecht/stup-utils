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
//  type PopupHideFunc[T] = { def hide(): Unit; def show[T](arg: T): Unit }
  type PopupHideFunc = { def hide(): Unit; def show[T](arg: T): Unit }

  // fields
  private var _activePopup: Option[PopupHideFunc] = None
//  private var _activePopup: Option[PopupHideFunc[_]] = None

  /**
   * Close currently open popup.
   */
  def close(): Unit = synchronized { hide() }

  /**
   * Activate the given popup.
   */
//  def show[T](popup: PopupHideFunc[T], arg: T): Unit = {
  def show[T](popup: PopupHideFunc, arg: T): Unit = {
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
