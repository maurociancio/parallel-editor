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
 * TODO a ser extraidos en el módulo cliente
 */
case class FromKernel(val msg: Any)
case class ToKernel(val msg: Any)
case class RegisterRemoteActor(val remote: Actor)