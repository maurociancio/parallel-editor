package ar.noxit.paralleleditor.common

import logger.Loggable

case class Message(val op: String, val myMsgs: Int, val otherMsgs: Int)

class JupiterSynchronizer extends Loggable {

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
    var outgoingMsgs = Map[Int, String]()

    def generateMsg(op: String) {
        applyOp(op)
        send((op, myMsgs, otherMsgs))

        outgoingMsgs = outgoingMsgs.update(myMsgs, op)
        myMsgs = myMsgs + 1
    }

    def receiveMsg(message: Message) {
        trace("recibido " + message)

        // filtro mensajes anteriores al recibido (acknowledged messages)
        outgoingMsgs = outgoingMsgs filterKeys (_ >= message.otherMsgs)

        //calculo la transformada de la operacion a realizar
        val finalOp = (message.op /: outgoingMsgs) {
            (transformedOp, currentListElement) => {
                val currentOp = currentListElement _2
                val transformatedOps = xform(currentOp, transformedOp)

                outgoingMsgs = outgoingMsgs.update(currentListElement _1, transformatedOps _1)

                transformatedOps _2
            }
        }

        applyOp(finalOp)
        otherMsgs = otherMsgs + 1
    }

    def applyOp(op: String) {
        println("Aplicando op " + op)
    }

    def send(op: (String, Int, Int)) {
        println("se envio " + op)
    }

    def xform(c: String, s: String) = {
        (c + "'", s + "'")
    }
}
