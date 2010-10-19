package ar.noxit.paralleleditor.gui

import org.springframework.context.support.ClassPathXmlApplicationContext

object Main {
    def main(args: Array[String]) {
        val xml = getXmlConfig(args)
        new ClassPathXmlApplicationContext(xml)
    }

    def getXmlConfig(args: Array[String]) = {
        if (args.size == 1) {
            args(0)
        } else {
            "gui.xml"
        }
    }
}
