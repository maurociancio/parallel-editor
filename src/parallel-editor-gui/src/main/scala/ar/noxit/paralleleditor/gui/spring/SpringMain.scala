package ar.noxit.paralleleditor.gui.spring

import org.springframework.beans.factory.InitializingBean
import ar.noxit.paralleleditor.gui.GUI
import reflect.BeanProperty

class SpringMain extends InitializingBean {
    @BeanProperty
    var mainWindow: GUI = _

    override def afterPropertiesSet = {
        mainWindow.main(null)
    }
}
