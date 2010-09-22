package ar.noxit.paralleleditor.gui

import swing._

object GUI extends SimpleSwingApplication{

  def top = new MainFrame{
    
    title = "Parallel Editor GUI"

    menuBar = new BarraMenuesGUI

    contents = new FlowPanel {
          contents += new TextArea("hola mundo",20,50)
    }

  }
}