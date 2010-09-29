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
    var outgoingMsgs: List[(String,Int)] = List()

    def generateMsg(op:String){
        
        applyOp(op)
       // send((op,myMsgs,otherMsgs))
        outgoingMsgs = (op,myMsgs) :: outgoingMsgs 

       myMsgs = myMsgs + 1
    }

    def receiveMsg( msg:(String,Int,Int) ) {

         // filtro mensajes anteriores al recibido (acknowledged messages)
        outgoingMsgs =  outgoingMsgs.filter( _._2 >= msg._3)

        // assert msg.myMsgs == otherMsgs
        outgoingMsgs.map( (msg:(String,Integer)) => _)

        applyOp(msg._1)
        
        otherMsgs = otherMsgs + 1
    }

    def applyOp(op:String){
        println("Aplicando op " + op)
    }

    //def send()

}