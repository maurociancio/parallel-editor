package ar.noxit.paralleleditor.kernel.basic

import actors.Actor

trait DocumentFactory {
    def newBasicDocument(actor: Actor): BasicDocument
}

class BasicDocumentFactory(val title: String, val contents: String) extends DocumentFactory {
    override def newBasicDocument(actor: Actor) = {
        new BasicDocument(title, contents, actor)
    }
}