package ar.noxit.paralelleditor.eclipse.views;

import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.DocumentDataAdapter;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.EclipseDocumentListener;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.IDocumentSession;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.RemoteMessageCallbackAdapter;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.Synchronizer;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession.ISubscriptionCallback;

public class SubscriptionCallback implements ISubscriptionCallback {

	@Override
	public void onDocumentListResponse(final String docTitle,
			final String initialContent,
			final IDocumentSession docSession) {

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				ITextEditor newEditor = openNewEditor(docTitle, initialContent);
				IDocument document = getDocumentFromEditor(newEditor);

				RemoteMessageCallbackAdapter remoteCallback = new RemoteMessageCallbackAdapter();

				// synchronizer
				Synchronizer sync = new Synchronizer(docSession, new DocumentDataAdapter(document, newEditor));

				// adapt
				remoteCallback.setAdapted(sync);

				// listener
				document.addDocumentListener(new EclipseDocumentListener(sync));
			}

			protected IDocument getDocumentFromEditor(ITextEditor textEditor) {
				IDocumentProvider documentProvider = textEditor.getDocumentProvider();
				return documentProvider.getDocument(textEditor.getEditorInput());
			}

			protected ITextEditor openNewEditor(final String docTitle, final String initialContent) {
				return (ITextEditor) EditorOpener.openNewEditor(docTitle, initialContent);
			}
		});
	}
}