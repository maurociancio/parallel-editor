package ar.noxit.paralleleditor.gui

import actors.Actor

/**
 * Mensajes que se envian entre la GUI y el GUI Actor
 */

/**
 * Le pide al kernel que lo desloguee
 */
case class Logout()

/**
 * Mensajes entre elg GuiActor y el RemoteServerProxy
 * TODO a ser extraidos en el módulo cliente
 */
case class FromKernel(val msg: Any)
case class ToKernel(val msg: Any)
case class RegisterRemoteActor(val remote: Actor)
