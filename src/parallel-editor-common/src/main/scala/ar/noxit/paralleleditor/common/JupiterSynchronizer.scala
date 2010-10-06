package ar.noxit.paralleleditor.common

import scala.List



class JupiterSynchronizer {

    /**
     * nº de  mensajes originados localmente
     */
    var myMsgs = 0

    /**
     * nº de  mensajes que llegaron del afuera
     */
    var otherMsgs = 0

    /**
     * lista de mensajes que se generaron y enviaron localmente
     */
    var outgoingMsgs: Map[Int,(String,Int)] = Map()

    def generateMsg(op:String){
        applyOp(op)
        send((op,myMsgs,otherMsgs))
        println("adding msg" + op)
        outgoingMsgs = outgoingMsgs.update(myMsgs,(op,myMsgs))
        myMsgs = myMsgs + 1
    }

    def receiveMsg( msg:(String,Int,Int) ) {

        println("recibido " + msg)
        // filtro mensajes anteriores al recibido (acknowledged messages)
        outgoingMsgs = outgoingMsgs filterKeys ( _ >= msg._3)

        //calculo la transformada de la operacion a realizar
        val finalOp = (msg._1 /: outgoingMsgs) {
            (s, c) => {
                val ot = xform(c._2._1,s)
                outgoingMsgs = outgoingMsgs.update(c _1,(ot._1, c _1))
                ot._2
            }
        }

        applyOp(finalOp)
        
        otherMsgs = otherMsgs + 1
    }

    def applyOp(op:String){
        println("Aplicando op " + op)
    }

    def send(op:(String,Int,Int)){
        println("se envio "+ op)
    }

    def xform(c:String,s:String) = {
        (c+"'",s+"'")
    }
}