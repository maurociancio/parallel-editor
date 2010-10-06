package ar.noxit.paralleleditor.common.network

import java.net.Socket
import java.io.{ObjectOutputStream, OutputStream, ObjectInputStream, InputStream}
import ar.noxit.paralleleditor.common.logger.Loggable

class SocketNetworkConnection(private val socket: Socket) extends NetworkConnection with Loggable {
    trace("Socket network connection created")

    override def messageOutput = new SocketMessageOutput(socket.getOutputStream)

    override def messageInput = new SocketMessageInput(socket.getInputStream)

    override def close = {
        trace("Network connection closed")
        socket.close
    }
}

class SocketMessageInput(private val inputStream: InputStream) extends MessageInput {
    private val objectInput = new ObjectInputStream(inputStream)

    override def readMessage = objectInput readObject
}

class SocketMessageOutput(private val outputStream: OutputStream) extends MessageOutput {
    private val objectOutput = new ObjectOutputStream(outputStream)

    override def writeMessage(message: Any) = objectOutput writeObject message
}
