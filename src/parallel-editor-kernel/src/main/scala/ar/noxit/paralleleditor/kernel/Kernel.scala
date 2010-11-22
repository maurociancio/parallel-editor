package ar.noxit.paralleleditor.kernel

trait Kernel {
    def login(username: String): Session
    def subscribe(session: Session, title: String)

    def documentList: List[String]
    def userList(session: Session)

    def newDocument(owner: Session, title: String, initialContent: String = "")
    def deleteDocument(session: Session, title: String)
    def removeDeletedDocument(title: String)

    def chat(from: Session, message: String)

    def terminate
}
