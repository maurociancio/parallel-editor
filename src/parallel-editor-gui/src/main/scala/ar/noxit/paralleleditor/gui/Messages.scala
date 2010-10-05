package ar.noxit.paralleleditor.gui

/**
 * Mensajes que se envian entre la GUI y el GUI Actor
 */

/**
 * Le pide que se loguee contra el kernel
 */
case class Login(val username: String)

/**
 * Le pide al kernel que lo desloguee
 */
case class Logout()

/**
 * Mensajes entre elg GuiActor y el RemoteServerProxy
 */
case class FromKernel(val msg: Any)
case class ToKernel(val msg: Any)
