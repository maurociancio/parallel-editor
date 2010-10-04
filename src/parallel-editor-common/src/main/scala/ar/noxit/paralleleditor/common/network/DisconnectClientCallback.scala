package ar.noxit.paralleleditor.common.network

import ar.noxit.paralleleditor.common.remote.Client

trait DisconnectClientCallback {
    def disconnect(client: Client)
}
