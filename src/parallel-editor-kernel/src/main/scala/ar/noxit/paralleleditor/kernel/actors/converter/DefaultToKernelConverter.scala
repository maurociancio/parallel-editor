package ar.noxit.paralleleditor.kernel.actors.converter

import ar.noxit.paralleleditor.kernel.actors.ToKernelConverter
import ar.noxit.paralleleditor.kernel.Session
import ar.noxit.paralleleditor.common.messages._
import ar.noxit.paralleleditor.kernel.messages._

class DefaultToKernelConverter extends ToKernelConverter {
    def convert(session: Session, toKernel: ToKernel) =
        toKernel match {
            case RemoteNewDocumentRequest(title, initialContent) =>
                NewDocumentRequest(session, title, initialContent)

            case RemoteDocumentListRequest() =>
                DocumentListRequest(session)

            case RemoteSubscribeRequest(title) =>
                SubscribeToDocumentRequest(session, title)

            case RemoteDeleteDocumentRequest(docTitle) =>
                CloseDocument(session, docTitle)

            case RemoteUserListRequest() =>
                UserListRequest(session)

            case RemoteSendChatMessage(message) =>
                ChatMessage(session, message)
        }
}
