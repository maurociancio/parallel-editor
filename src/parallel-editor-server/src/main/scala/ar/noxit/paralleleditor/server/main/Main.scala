package ar.noxit.paralleleditor.server.main

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
            "server.xml"
        }
    }
}
