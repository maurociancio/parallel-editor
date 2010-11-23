package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;

import scala.collection.immutable.Nil$;
import ar.noxit.paralleleditor.common.operation.AddTextOperation;
import ar.noxit.paralleleditor.common.operation.DeleteTextOperation;
import ar.noxit.paralleleditor.common.operation.EditOperation;

public class BaseEclipseDocumentListener implements IDocumentListener {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static scala.collection.immutable.List<Integer> getNilFromScala() {
		Nil$ module = Nil$.MODULE$;
		return (scala.collection.immutable.List) module;
	}

	@Override
	public void documentChanged(DocumentEvent event) {
		Assert.isNotNull(event);

		final int replacedTextLength = event.fLength;
		final int offset = event.fOffset;
		final String text = event.fText;
		final int textLength = text != null ? text.length() : 0;

		if (replacedTextLength == 0) {
			// largo del reemplazo cero, no es reeplazo ni borrado => inserción
			createAddOperations(text, offset);
		} else {
			// borramos de a un caracter
			createDeleteOperations(replacedTextLength, offset);

			if (textLength == 0) {
				// solo borrado
			} else {
				// reemplazo, generamos operaciones de borrado (arriba generadas
				// y de insercion)
				createAddOperations(text, offset);
			}
		}
	}

	private void createDeleteOperations(final int replacedTextLength, final int offset) {
		for (int i = 0; i < replacedTextLength; i++) {
			processOperation(new DeleteTextOperation(offset, 1));
		}
	}

	protected void processOperation(EditOperation editOperation) {
	}

	protected void createAddOperations(String text, int offset) {
		for (int i = 0; i < text.length(); i++) {
			processOperation(new AddTextOperation(text.substring(i, i + 1), offset + i, getNilFromScala()));
		}
	}

	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
		// nothing to do
	}
}