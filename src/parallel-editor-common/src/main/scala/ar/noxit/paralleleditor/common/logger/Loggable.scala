/*
 *  A real-time collaborative tool to develop files over the network.
 *  Copyright (C) 2010  Mauro Ciancio and Leandro Gilioli
 *                      {maurociancio,legilioli} at gmail dot com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
