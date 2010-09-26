package ar.noxit.paralleleditor.kernel.messages

import ar.noxit.paralleleditor.kernel.Session

case class SilentUnsubscribe(session: Session)
case class Subscribe(who: Session)
case class SubscriberCount
case class Unsubscribe(who: Session)