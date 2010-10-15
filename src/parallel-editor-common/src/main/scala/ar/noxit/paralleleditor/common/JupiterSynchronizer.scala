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
    def generateMsg(op: Op, send: Message[Op] => Unit) {
        trace("Generating message")

        // enviar mensaje a la otra parte
        send(Message(op, _myMsgs, _otherMsgs))

        outgoingMsgs = outgoingMsgs.update(_myMsgs, op)
        _myMsgs = _myMsgs + 1

        trace("estado actual %d %d", _myMsgs, _otherMsgs)
    }

    def receiveMsg(message: Message[Op], apply: Op => Unit) {
        trace("Message received " + message)

        // filtro mensajes anteriores al recibido (acknowledged messages)
        outgoingMsgs = outgoingMsgs filterKeys (_ >= message.otherMsgs)

        trace("original op %s", message.op)
        // calculo la transformada de la operacion a realizar
        val finalOp = (message.op /: outgoingMsgs) {
            (transformedOp, currentListElement) => {
                val currentOp = currentListElement _2
                val transformatedOps = xform(currentOp, transformedOp)

                outgoingMsgs = outgoingMsgs.update(currentListElement _1, transformatedOps _1)

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
