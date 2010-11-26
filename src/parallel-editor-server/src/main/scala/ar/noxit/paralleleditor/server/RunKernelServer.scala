/*
 *  A real-time collaborative tool to develop files over the network.
 *  Copyright (C) 2010  Mauro Ciancio and Leandro Gilioli
 *                      {maurociancio,legilioli} at gmail dot com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ar.noxit.paralleleditor.server

import ar.noxit.paralleleditor.kernel.remote.{KernelService, SocketKernelService}
import reflect.BeanProperty
import org.springframework.beans.factory.{BeanFactory, BeanFactoryAware, InitializingBean}
import ar.noxit.paralleleditor.common.remote.PeerActorFactory

class DefaultKernelService(private val port: Int) extends SocketKernelService(port) with BeanFactoryAware {
    private var bf: BeanFactory = _

    override protected def newClientActorFactory =
        bf.getBean("clientActorFactory", ka).asInstanceOf[PeerActorFactory]

    def setBeanFactory(beanFactory: BeanFactory) = this.bf = beanFactory
}

class RunKernelServer extends InitializingBean {
    @BeanProperty
    var service: KernelService = _

    def afterPropertiesSet =
        service.startService
}
