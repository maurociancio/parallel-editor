package ar.noxit.paralleleditor.gui


/**
 * Mensajes que se envian entre el GUI Actor y el Remote Server Proxy
 */

/**
 * Le pide que se loguee contra el kernel
 */
case class Login(val username: String)

/**
 * Le pide al kernel que lo desloguee
 */
case class Logout()
