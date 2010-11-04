package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.kernel.Session

trait UserListMerger {
    def notifyUserList(to: Session, sessions: List[Session], documents: List[DocumentActor])
}
