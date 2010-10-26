package ar.noxit.paralleleditor.kernel.actors

import ar.noxit.paralleleditor.kernel.messages.ToRemote
import ar.noxit.paralleleditor.common.messages.BaseRemoteMessage

trait RemoteMessageConverter {
    def convert(remote: ToRemote): BaseRemoteMessage
}
