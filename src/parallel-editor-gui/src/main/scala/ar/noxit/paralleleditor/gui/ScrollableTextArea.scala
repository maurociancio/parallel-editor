package ar.noxit.paralleleditor.gui

import swing.{Component, TextArea, ScrollPane}

/**
 * Created by IntelliJ IDEA.
 * User: legilioli
 * Date: 23/09/2010
 * Time: 23:41:32
 * To change this template use File | Settings | File Templates.
 */

class ScrollableTextArea(val c:Component ) extends ScrollPane(c) {

  val textArea = c
  horizontalScrollBarPolicy_=(ScrollPane.BarPolicy.AsNeeded)

  def this() = {
     this(new TextArea("hola mundo", 30, 50))
  }
  
}