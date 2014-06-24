package de.windelknecht.stup.utils.io

import java.io.{OutputStream, InputStream}
import java.nio.channels.{Channels, WritableByteChannel, ReadableByteChannel}
import java.nio.ByteBuffer

object ChannelTools {
  /**
   * Copies the content of the src channel into the dest channel.
   */
  def fastChannelCopy(
    size: Int = Int.MaxValue,
    src: ReadableByteChannel,
    dest: WritableByteChannel
    ) {
    val buffer = ByteBuffer.allocateDirect(Math.min(size, 16 * 1024))
    var alreadyRead = 0L

    while (src.read(buffer) != -1) {
      // prepare the buffer to be drained
      buffer.flip()

      buffer.limit(math.min((size - alreadyRead).toInt, buffer.limit()))
      alreadyRead += buffer.limit()

      // write to the channel, may block
      dest.write(buffer)
      // If partial transfer, shift remainder down
      // If buffer is empty, same as doing clear()
      buffer.compact()

      if(alreadyRead >= size)
        return
    }
    // EOF will leave buffer in fill state
    buffer.flip()
    // make sure the buffer is fully drained.
    while (buffer.hasRemaining) {
      dest.write(buffer)
    }
  }

  /**
   * Copies the given src input stream into the given output stream.
   */
  def fastStreamCopy(
    src: InputStream,
    dest: OutputStream
    ) {
    val inputChannel = Channels.newChannel(src)
    val outputChannel = Channels.newChannel(dest)

    ChannelTools.fastChannelCopy(src = inputChannel, dest = outputChannel)

    // cleanup
    inputChannel.close()
    outputChannel.close()
  }

  /**
   * Copies the given src input stream into the given output stream.
   */
  def fastStreamCopy_doNotCloseInput(
    size: Long,
    src: InputStream,
    dest: OutputStream
    ) {
    val inputChannel = Channels.newChannel(src)
    val outputChannel = Channels.newChannel(dest)

    ChannelTools.fastChannelCopy(src = inputChannel, dest = outputChannel)

    // cleanup
    outputChannel.close()
  }

  /**
   * Copies the given src input stream into the given output stream.
   */
  def fastStreamCopy_doNotCloseOutput(
    src: InputStream,
    dest: OutputStream
    ) {
    val inputChannel = Channels.newChannel(src)
    val outputChannel = Channels.newChannel(dest)

    ChannelTools.fastChannelCopy(src = inputChannel, dest = outputChannel)

    // cleanup
    inputChannel.close()
  }
}
