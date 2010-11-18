package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.texteditor.ITextEditor;

import ar.noxit.paralleleditor.common.operation.DocumentData;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.IDocument;
import ar.noxit.paralleleditor.eclipse.menu.actions.IShareLocalDocumentIntent;
import ar.noxit.paralleleditor.eclipse.views.EditorOpener;
import ar.noxit.paralleleditor.eclipse.views.IRemoteDocumentShare;

public abstract class AbstractShareDocumentIntent implements IShareLocalDocumentIntent, IRemoteDocumentShare {

	private IShareManager shareManager;

	public AbstractShareDocumentIntent(IShareManager shareManager) {
		Assert.isNotNull(shareManager);

		this.shareManager = shareManager;
	}

	@Override
	public void shareDocument(IDocument document) {
		Assert.isNotNull(document);

		ITextEditor textEditor = document.getTextEditor();
		String path = document.getFullPath().toString();

		// callback from kernel
		RemoteMessageCallbackAdapter remoteCallback = new RemoteMessageCallbackAdapter();

		// create the new document session
		IDocumentSession docSession = shareManager.createLocalShare(path, getContentFor(textEditor), remoteCallback);

		// callback from editor
		Synchronizer sync = new Synchronizer(docSession, getAdapterFor(textEditor));

		// adapt callbacks
		remoteCallback.setAdapted(sync);

		// install callback on editor
		installCallback(textEditor, new EclipseDocumentListener(sync));
	}

	@Override
	public void shareRemoteDocument(String docTitle, String initialContent, IDocumentSession docSession) {
		Assert.isNotNull(docTitle);
		Assert.isNotNull(initialContent);

		ITextEditor textEditor = openNewEditor(docTitle, initialContent);

		// synchronizer
		Synchronizer sync = new Synchronizer(docSession, getAdapterFor(textEditor));

		// adapt
		docSession.installCallback(sync);

		// listener
		installCallback(textEditor, new EclipseDocumentListener(sync));
	}

	private ITextEditor openNewEditor(String docTitle, String initialContent) {
		return (ITextEditor) EditorOpener.openNewEditor(docTitle, initialContent);
	}

	protected abstract DocumentData getAdapterFor(ITextEditor textEditor);

	protected abstract String getContentFor(ITextEditor textEditor);

	protected abstract void installCallback(ITextEditor textEditor, IDocumentListener listener);
}
