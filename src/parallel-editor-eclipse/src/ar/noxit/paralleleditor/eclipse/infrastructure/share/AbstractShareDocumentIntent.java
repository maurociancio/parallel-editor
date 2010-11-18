package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.IPartService;
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
		IPartService partService = textEditor.getSite().getWorkbenchWindow().getPartService();
		partService.addPartListener(new TextEditorClosedListener(partService, textEditor, docSession));
	}

	private ITextEditor openNewEditor(String docTitle, String initialContent) {
		return (ITextEditor) EditorOpener.openNewEditor(docTitle, initialContent);
	}

	private ITextEditorDisabler adapt(ITextEditor textEditor) {
		return new TextEditorDisabler(textEditor);
	}

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
