package ar.noxit.paralleleditor.common

import logger.Loggable
import operation.EditOperation

case class Message[Op](val op: Op, val myMsgs: Int, val otherMsgs: Int)

abstract class JupiterSynchronizer[Op] extends Loggable {
    /**
     * nº de  mensajes originados localmente
     */
    private var myMsgs = 0

    /**
     * nº de  mensajes que llegaron del afuera
     */
    private var otherMsgs = 0

    /**
     * lista de mensajes que se generaron y enviaron localmente
     */
    private var outgoingMsgs = Map[Int, Op]()

    def generateMsg(op: Op, apply: Op => Unit = op => {}) {
        trace("Generating message")

        // aplicar operacion localmente
        apply(op)

        // enviar mensaje a la otra parte
        send(Message(op, myMsgs, otherMsgs))

        outgoingMsgs = outgoingMsgs.update(myMsgs, op)
        myMsgs = myMsgs + 1
    }

    def receiveMsg(message: Message[Op], apply: Op => Unit) {
        trace("Message received " + message)

        // filtro mensajes anteriores al recibido (acknowledged messages)
        outgoingMsgs = outgoingMsgs filterKeys (_ >= message.otherMsgs)

        // calculo la transformada de la operacion a realizar
        val finalOp = (message.op /: outgoingMsgs) {
            (transformedOp, currentListElement) => {
                val currentOp = currentListElement _2
                val transformatedOps = xform(currentOp, transformedOp)

                outgoingMsgs = outgoingMsgs.update(currentListElement _1, transformatedOps _1)

                transformatedOps _2
            }
        }

        apply(finalOp)

        otherMsgs = otherMsgs + 1
    }

    protected def send(message: Message[Op])

    protected def xform(c: Op, s: Op): (Op, Op)
}

class EditOperationJupiterSynchronizer(private val xformStrategy: XFormStrategy) extends JupiterSynchronizer[EditOperation] {
    override protected def xform(c: EditOperation, s: EditOperation) = {
        xformStrategy.xform(c, s)
    }

    override protected def send(message: Message[EditOperation]) {
        println("sending msg", message)
    }
}
