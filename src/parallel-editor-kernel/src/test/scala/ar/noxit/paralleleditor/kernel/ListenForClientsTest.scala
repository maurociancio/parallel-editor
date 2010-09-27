package ar.noxit.paralleleditor.kernel

import actors.{ClientActor, KernelActor}
import basic.BasicKernel
import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import remote.{BasicClientActorFactory, RemoteClientProxy}
import scala.actors.Actor
import java.net.{Socket, ServerSocket}

/**
 * Created by IntelliJ IDEA.
 * User: legilioli
 * Date: 26/09/2010
 * Time: 23:48:14
 * To change this template use File | Settings | File Templates.
 */

@Test
class ListenForClientsTest extends AssertionsForJUnit {


    @Test
    def testIncomingConnection:Unit = {


        kernel = new BasicKernel
        ka = new KernelActor(kernel).start
        clientFactory = new BasicClientActorFactory(ka)

        // create a server socket
        val serverSocket = new ServerSocket(5000);


        connectionsListener = actor {
            // create a socket object from the serversocket object
            val socket = serverSocket.accept();

            // connection notification
            System.out.println("Cliente nuevo");

                  val remoteClientProxy  = new RemoteClientProxy(socket)
                  cliente = new ClientActor(ka,remoteClientProxy.gateway)
                  cliente start

        }

    }

}