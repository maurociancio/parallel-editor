package ar.noxit.paralleleditor.kernel

import actors.{ClientActor, KernelActor}
import basic.BasicKernel
import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import remote.{BasicClientActorFactory, RemoteClientProxy}
import scala.actors.Actor
import java.net.{Socket, ServerSocket}

@Test
class ListenForClientsTest extends AssertionsForJUnit {

    @Test
    def testIncomingConnection:Unit = {
        val kernel = new BasicKernel
        val ka = new KernelActor(kernel).start

        // abro un socket
        val serverSocket = new ServerSocket(5000);

        // acepto conexiones
        val socket = serverSocket.accept();
        System.out.println("Cliente nuevo");

        val clientActorFactory = new BasicClientActorFactory(ka)
        //instancio un proxy de cliente remoto a partir de la conexion recibida
        val remoteClientProxy = new RemoteClientProxy(socket, clientActorFactory)

        //creo un actorCliente a partir del proxy de cliente remoto
        val cliente = new ClientActor(ka, remoteClientProxy.gateway)
        cliente start
    }
}
