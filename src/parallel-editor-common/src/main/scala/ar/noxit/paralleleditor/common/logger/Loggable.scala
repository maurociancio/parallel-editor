package ar.noxit.paralleleditor.common.logger

import org.slf4j.{LoggerFactory, Logger}

trait Loggable {
    val logger: Logger = Logging.getLogger(this)

    def checkFormat(msg:String, refs:Seq[Any]): String =
        if (refs.size > 0) msg.format(refs: _*) else msg

    def trace(msg:String, refs:Any*) = logger trace checkFormat(msg, refs)

    def trace(t:Throwable, msg:String, refs:Any*) = logger trace (checkFormat(msg, refs), t)

    def info(msg:String, refs:Any*) = logger info checkFormat(msg, refs)

    def info(t:Throwable, msg:String, refs:Any*) = logger info (checkFormat(msg, refs), t)

    def warn(msg:String, refs:Any*) = logger warn checkFormat(msg, refs)

    def warn(t:Throwable, msg:String, refs:Any*) = logger warn (checkFormat(msg, refs), t)

    def critical(msg:String, refs:Any*) = logger error checkFormat(msg, refs)

    def critical(t:Throwable, msg:String, refs:Any*) = logger error (checkFormat(msg, refs), t)
}

object Logging {
    def loggerNameForClass(className: String) = {
        if (className endsWith "$")
            className.substring(0, className.length - 1)
        else
            className
    }

    def getLogger(logging: AnyRef) = LoggerFactory.getLogger(loggerNameForClass(logging.getClass.getName))
}
