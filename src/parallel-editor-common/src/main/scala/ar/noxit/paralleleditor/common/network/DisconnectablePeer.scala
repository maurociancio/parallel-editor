package ar.noxit.paralleleditor.common.network

import ar.noxit.paralleleditor.common.remote.Peer

trait DisconnectablePeer {
    def disconnect(peer: Peer)
}
