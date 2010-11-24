package ar.noxit.paralleleditor.eclipse.share.sync;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.texteditor.ITextEditor;

import ar.noxit.paralleleditor.common.operation.DocumentData;
import ar.noxit.paralleleditor.eclipse.editor.EditorOpener;
import ar.noxit.paralleleditor.eclipse.editor.listeners.EclipseDocumentListener;
import ar.noxit.paralleleditor.eclipse.menu.IShareLocalDocumentIntent;
import ar.noxit.paralleleditor.eclipse.share.IDocument;
import ar.noxit.paralleleditor.eclipse.views.share.IRemoteDocumentShare;
import ar.noxit.paralleleditor.eclipse.views.share.IShareManager;

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

		// set up callbacks
		setUpCallbacks(textEditor, new RemoteMessageCallbackInstaller(remoteCallback), docSession);
	}

	@Override
	public void shareRemoteDocument(String docTitle, String initialContent, IDocumentSession docSession) {
		Assert.isNotNull(docTitle);
		Assert.isNotNull(initialContent);

		// open a new editor
		ITextEditor textEditor = openNewEditor(docTitle, initialContent);

		// set up callbacks
		setUpCallbacks(textEditor, new DocumentSessionCallbackInstaller(docSession), docSession);
	}

	private void setUpCallbacks(ITextEditor textEditor, CallbackInstaller installer, IDocumentSession docSession) {
		// callback from editor
		Synchronizer sync = new Synchronizer(docSession, getAdapterFor(textEditor), adapt(textEditor));

		// adapt callbacks
		installer.install(sync);

		// install callback on editor
		installCallback(textEditor, new EclipseDocumentListener(sync));

		// install callback for document close
		installOnCloseTextEditorCallback(textEditor, docSession);
	}

	protected abstract void installOnCloseTextEditorCallback(ITextEditor textEditor, IDocumentSession docSession);

	private ITextEditor openNewEditor(String docTitle, String initialContent) {
		return EditorOpener.openFileFromWorkspace(docTitle, initialContent);
	}

	protected abstract ITextEditorDisabler adapt(ITextEditor textEditor);

	protected abstract DocumentData getAdapterFor(ITextEditor textEditor);

	protected abstract String getContentFor(ITextEditor textEditor);

	protected abstract void installCallback(ITextEditor textEditor, IDocumentListener listener);

	// internal interfaces
	private static interface CallbackInstaller {
		void install(IRemoteMessageCallback callback);
	}

	private static class RemoteMessageCallbackInstaller implements CallbackInstaller {

		private RemoteMessageCallbackAdapter adapter;

		public RemoteMessageCallbackInstaller(RemoteMessageCallbackAdapter adapter) {
			this.adapter = adapter;
		}

		@Override
		public void install(IRemoteMessageCallback callback) {
			adapter.setAdapted(callback);
		}
	}

	private static class DocumentSessionCallbackInstaller implements CallbackInstaller {

		private IDocumentSession docSession;

		public DocumentSessionCallbackInstaller(IDocumentSession docSession) {
			this.docSession = docSession;
		}

		@Override
		public void install(IRemoteMessageCallback callback) {
			docSession.installCallback(callback);
		}
	}
}
