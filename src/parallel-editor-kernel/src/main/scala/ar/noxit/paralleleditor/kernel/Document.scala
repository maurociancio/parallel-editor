package ar.noxit.paralleleditor.kernel

trait Document {

    def suscribe(session: Session) : DocumentSession
}