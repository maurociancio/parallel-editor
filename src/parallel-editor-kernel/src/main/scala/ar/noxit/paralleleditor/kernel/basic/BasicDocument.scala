package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.kernel.Session
import ar.noxit.paralleleditor.kernel.Document
import scala.List

class BasicDocument(val title: String, var content: String) extends Document {

    var suscribers: List[Session] =  List()

    def suscribe(session: Session) = {
        // TODO validar que la session no sea null ni que ya esté en la lista de suscriptores
        suscribers = session :: suscribers
    }
}