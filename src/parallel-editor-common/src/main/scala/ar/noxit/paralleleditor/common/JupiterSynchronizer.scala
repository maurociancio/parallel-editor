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
package ar.noxit.paralleleditor.common

import logger.Loggable
import operation.EditOperation

case class Message[Op](val op: Op, val myMsgs: Int, val otherMsgs: Int)

abstract class JupiterSynchronizer[Op] extends Loggable {
    /**
     * nº de  mensajes originados localmente
     */
    private var _myMsgs = 0

    /**
     * nº de  mensajes que llegaron del afuera
     */
    private var _otherMsgs = 0

    /**
     * lista de mensajes que se generaron y enviaron localmente
     */
    private var outgoingMsgs = Map[Int, Op]()

    /**
     * Getter para testing
     */
    def myMsgs = _myMsgs

    /**
     * Getter para testing
     */
    def otherMsgs = _otherMsgs

    /**
     * La operación ya fue aplicada antes de llamarse a este método
     */
    def generate(op: Op, send: Message[Op] => Unit) {
        trace("Generating message")

        // enviar mensaje a la otra parte
        send(Message(op, _myMsgs, _otherMsgs))

        outgoingMsgs = outgoingMsgs.updated(_myMsgs, op)
        _myMsgs = _myMsgs + 1

        trace("estado actual %d %d", _myMsgs, _otherMsgs)
    }

    def receive(message: Message[Op], apply: Op => Unit) {
        trace("Message received " + message)

        // filtro mensajes anteriores al recibido (acknowledged messages)
        outgoingMsgs = outgoingMsgs filterKeys (_ >= message.otherMsgs)

        val ordenedMsgs = outgoingMsgs.toArray.sortBy {_._1}

        trace("original op %s", message.op)
        // calculo la transformada de la operacion a realizar
        val finalOp = (message.op /: ordenedMsgs) {
            (transformedOp, currentListElement) => {
                val currentOp = currentListElement _2
                val transformatedOps = xform(currentOp, transformedOp)

                outgoingMsgs = outgoingMsgs.updated(currentListElement _1, transformatedOps _1)

                transformatedOps _2
            }
        }
        trace("fixed op %s", finalOp)
        apply(finalOp)

        _otherMsgs = _otherMsgs + 1
        trace("estado actual %d %d", _myMsgs, _otherMsgs)
    }

    protected def xform(c: Op, s: Op): (Op, Op)
}

class EditOperationJupiterSynchronizer(private val xformStrategy: XFormStrategy) extends JupiterSynchronizer[EditOperation] {
    override protected def xform(c: EditOperation, s: EditOperation) = xformStrategy.xform(c, s)
}

trait SendFunction {
    def send(message: Message[EditOperation])
}

trait ApplyFunction {
    def apply(operation: EditOperation)
}

class JEditOperationJupiterSynchronizer(val adaptedSync: JupiterSynchronizer[EditOperation]) {
    def generate(op: EditOperation, fun: SendFunction) =
        adaptedSync.generate(op, {m => fun.send(m)})

    def receive(message: Message[EditOperation], fun: ApplyFunction) =
        adaptedSync.receive(message, {op => fun.apply(op)})
}
