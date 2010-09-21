package ar.noxit.paralleleditor.gui

import swing._

/**
 * Created by IntelliJ IDEA.
 * User: legilioli
 * Date: 21/09/2010
 * Time: 17:57:37
 * To change this template use File | Settings | File Templates.
 */


object GUI extends SimpleSwingApplication{

  def top = new MainFrame{
    
    title = "Parallel Editor GUI"

    menuBar = new BarraMenuesGUI

    contents = new FlowPanel {
          contents += new TextArea("hola mundo",20,50)
    }

  }

}