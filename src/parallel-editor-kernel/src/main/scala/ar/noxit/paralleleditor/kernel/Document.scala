package ar.noxit.paralleleditor.kernel

trait Document {

    val title: String
    def subscribe(session: Session) : DocumentSession
}
