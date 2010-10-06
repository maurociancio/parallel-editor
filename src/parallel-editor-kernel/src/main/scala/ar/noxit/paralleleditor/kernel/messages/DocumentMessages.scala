package ar.noxit.paralleleditor.kernel.messages

import ar.noxit.paralleleditor.kernel.{EditOperation, Session}

/**
 * Mensajes que se envian entre el actor del documento y el kernel.
 */

case class ProcessOperation(val who: Session, val operation: EditOperation)

case class SubscriberCount
case class Subscribe(val who: Session)
case class Unsubscribe(val who: Session)
case class SilentUnsubscribe(val session: Session)
