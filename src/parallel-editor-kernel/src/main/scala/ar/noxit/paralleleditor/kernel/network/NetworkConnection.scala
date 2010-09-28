package ar.noxit.paralleleditor.kernel.network

trait Closeable {
    def close
}

trait MessageInput {
    def readMessage: Any
}

trait MessageOutput {
    def writeMessage(message: Any)
}

trait NetworkConnection extends Closeable {
    def messageInput: MessageInput

    def messageOutput: MessageOutput

    def close
}
