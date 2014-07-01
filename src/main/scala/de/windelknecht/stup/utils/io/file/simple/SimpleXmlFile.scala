package de.windelknecht.stup.utils.io.file.simple

import scala.collection.mutable
import scala.xml.{Text, Null, Attribute, PrettyPrinter}

object SimpleXmlFile{
  /**
   * Create-fn for easier java usage.
   */
  def create(
    dataProvider: DataProvider
  ) = new SimpleXmlFile(dataProvider)
}

class SimpleXmlFile(
  dataProvider: DataProvider,
  nodeName: String = "property",
  attrName: String = "name"
  ) {
  // fields
  private val _values = new mutable.HashMap[String, String]()

  /**
   * Check if the xml file contains this key
   */
  def contains(key: String) = _values.contains(key)

  /**
   * Read complete content of data provider.
   * This must be called from user code, because the xml data could be invalid, so an exception is thrown.
   * And the user must be able to detect.
   */
  def read(): SimpleXmlFile = doRead()

  /**
   * Is xml file empty?
   */
  def isEmpty = _values.isEmpty

  /**
   * Read a xml node value
   */
  def get(key: String) = _values.getOrElse(key, "")

  /**
   * Write a xml node value
   */
  def set(
    key: String,
    value: String
    ) = {
    _values.synchronized {
      _values += (key -> value)
    }
    doWrite()
    this
  }

  /**
   * Called with read content
   */
  protected def doRead(): SimpleXmlFile = {
    val content = dataProvider.read().mkString("\n")

    if(content.isEmpty)
      return this

    val root = scala.xml.XML.loadString(content)

    _values.synchronized {
      _values.clear()
      _values ++= ((root \ nodeName) map { child => (child \ s"@$attrName").text -> child.text})
    }
    this
  }

  /**
   * Called when content can be written
   */
  protected def doWrite() {
    val content = _values.synchronized {
      <root>
        {_values.toSeq.sortBy(_._1).map { case (k, v) => <property>{v}</property>.copy(label = nodeName) % Attribute(None, attrName, Text(k), Null) }}
      </root>
    }

    dataProvider.write(new PrettyPrinter(80, 2).format(content))
  }
}
