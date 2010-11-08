package ar.noxit.paralleleditor.gui

import java.io.{FileWriter, File}

object FileHelper {
    implicit def file2helper(file: File) = new FileHelper(file)
}

class FileHelper(val file: File) {
    def write(content: String): Unit = {
        val fw = new FileWriter(file)
        try fw.write(content)
        finally fw.close
    }
}
