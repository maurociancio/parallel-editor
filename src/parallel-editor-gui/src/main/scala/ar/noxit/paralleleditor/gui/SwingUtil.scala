package ar.noxit.paralleleditor.gui

object SwingUtil {
    def invokeLater[X](exp: => X) {
        import javax.swing.SwingUtilities

        SwingUtilities invokeLater (new Runnable() {
            def run = exp
        })
    }
}
