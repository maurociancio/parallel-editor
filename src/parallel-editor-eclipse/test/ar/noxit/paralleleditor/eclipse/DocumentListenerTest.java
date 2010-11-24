package ar.noxit.paralleleditor.eclipse;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ar.noxit.paralleleditor.common.operation.AddTextOperation;
import ar.noxit.paralleleditor.common.operation.DeleteTextOperation;
import ar.noxit.paralleleditor.common.operation.EditOperation;
import ar.noxit.paralleleditor.eclipse.editor.listeners.BaseEclipseDocumentListener;

@Test
public class DocumentListenerTest {

	@BeforeMethod
	public void setup() {
	}

	@Test
	public void testAddTextOperation() {
		SpyEclipseDocumentListener listener = new SpyEclipseDocumentListener();

		IDocument document = createMock(IDocument.class);
		replay(document);

		int offset = 0;
		int replacedTextLength = 0;
		String substitutionText = "hola";
		listener.documentChanged(new DocumentEvent(document, offset, replacedTextLength, substitutionText));

		Assert.assertEquals(1, listener.editOperations.size());
		Assert.assertEquals(new AddTextOperation("hola", 0, BaseEclipseDocumentListener.getNilFromScala()),
				listener.editOperations.get(0));

		verify(document);
	}

	@Test
	public void testDeleteTextOperation() {
		SpyEclipseDocumentListener listener = new SpyEclipseDocumentListener();

		IDocument document = createMock(IDocument.class);
		replay(document);

		int offset = 0;
		int replacedTextLength = 10;
		String substitutionText = "";
		listener.documentChanged(new DocumentEvent(document, offset, replacedTextLength, substitutionText));

		Assert.assertEquals(10, listener.editOperations.size());
		for (int i = 0; i < 10; i++)
			Assert.assertEquals(new DeleteTextOperation(0, 1), listener.editOperations.get(i));

		verify(document);
	}

	@Test
	public void testReplaceTextOperation() {
		SpyEclipseDocumentListener listener = new SpyEclipseDocumentListener();

		IDocument document = createMock(IDocument.class);
		replay(document);

		int offset = 0;
		int replacedTextLength = 10;
		String substitutionText = "hola";
		listener.documentChanged(new DocumentEvent(document, offset, replacedTextLength, substitutionText));

		Assert.assertEquals(11, listener.editOperations.size());
		for (int i = 0; i < 10; i++)
			Assert.assertEquals(new DeleteTextOperation(0, 1), listener.editOperations.get(i));
		Assert.assertEquals(new AddTextOperation("hola", 0, BaseEclipseDocumentListener.getNilFromScala()),
				listener.editOperations.get(10));

		verify(document);
	}

	private final class SpyEclipseDocumentListener extends BaseEclipseDocumentListener {
		private List<EditOperation> editOperations = new ArrayList<EditOperation>();

		@Override
		protected void processOperation(EditOperation editOperation) {
			editOperations.add(editOperation);
		}
	}
}
