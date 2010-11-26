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
package ar.noxit.paralleleditor.client

import actors.Actor

/**
 * Mensajes que se envian entre un Cliente y el kernel
 */

/**
 * Le pide al kernel que lo desloguee
 */
case class Logout()

/**
 * Mensajes entre elg ClientActor y el RemoteServerProxy
 */
case class FromKernel(val msg: Any)
case class ToKernel(val msg: Any)
case class RegisterRemoteActor(val remote: Actor)
