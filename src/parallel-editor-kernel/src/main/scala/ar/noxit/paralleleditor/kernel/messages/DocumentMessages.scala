package ar.noxit.paralleleditor.kernel.messages

import ar.noxit.paralleleditor.kernel.Session
import ar.noxit.paralleleditor.common.operation.EditOperation

/**
 * Mensajes que se envian entre el actor del documento y el kernel.
 */

case class ProcessOperation(val who: Session, val operation: EditOperation)

case class SubscriberCount
case class Subscribe(val who: Session)
case class Unsubscribe(val who: Session)
case class SilentUnsubscribe(val session: Session)

/**
 * La envia el doc actor para que el client actor la retransmita al cliente remoto
 */
case class PublishOperation(val docTitle: String, val operation: EditOperation)
