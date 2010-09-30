package ar.noxit.paralleleditor.kernel.messages

import ar.noxit.paralleleditor.kernel.{EditOperation, Session}

case class ProcessOperation(who: Session, operation: EditOperation)
case class SilentUnsubscribe(session: Session)
case class Subscribe(who: Session)
case class SubscriberCount
case class Unsubscribe(who: Session)
