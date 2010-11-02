package ar.noxit.paralleleditor.kernel

trait Kernel {
    def login(username: String): Session

    def documentList: List[String]
    def newDocument(owner: Session, title: String, initialContent: String = "")
    def deleteDocument(session: Session, title: String)
    def removeDeletedDocument(title: String)

    def subscribe(session: Session, title: String)
}
