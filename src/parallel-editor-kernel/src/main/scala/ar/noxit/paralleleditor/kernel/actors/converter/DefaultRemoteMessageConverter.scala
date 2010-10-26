package ar.noxit.paralleleditor.kernel.actors.converter

import ar.noxit.paralleleditor.kernel.actors.RemoteMessageConverter
import ar.noxit.paralleleditor.kernel.messages.{SubscriptionNotExists, DocumentListResponse, SubscriptionAlreadyExists, ToRemote}
import ar.noxit.paralleleditor.common.messages.{RemoteDocumentListResponse, RemoteDocumentSubscriptionNotExists, RemoteDocumentSubscriptionAlreadyExists}

class DefaultRemoteMessageConverter extends RemoteMessageConverter {
    override def convert(remote: ToRemote) = {
        remote match {
            case SubscriptionAlreadyExists(offenderTitle) =>
                RemoteDocumentSubscriptionAlreadyExists(offenderTitle)

            case SubscriptionNotExists(offenderTitle) =>
                RemoteDocumentSubscriptionNotExists(offenderTitle)

            case DocumentListResponse(docList) =>
                RemoteDocumentListResponse(docList)
        }
    }
}
