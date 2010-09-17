package ar.noxit.paralleleditor.kernel

trait UpdateCallback {

    /**
     * 
     * @param message is the change introduced.
     *        AnyRef is like Java's Object
     */
    def update(message: AnyRef)
}