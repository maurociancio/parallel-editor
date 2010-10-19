package ar.noxit.paralleleditor.gui.spring

import org.springframework.beans.factory.InitializingBean
import ar.noxit.paralleleditor.gui.GUI

class SpringMain extends InitializingBean {
    override def afterPropertiesSet = {
        GUI.main(null)
    }
}
