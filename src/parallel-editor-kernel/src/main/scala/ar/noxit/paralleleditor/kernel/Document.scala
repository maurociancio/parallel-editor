package ar.noxit.paralleleditor.kernel

trait Document {

    def subscribe(session: Session) : DocumentSession
}