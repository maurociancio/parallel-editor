package ar.noxit.paralleleditor.eclipse.views.share;

import ar.noxit.paralleleditor.eclipse.share.sync.IDocumentSession;
import ar.noxit.paralleleditor.eclipse.share.sync.IRemoteMessageCallback;

public interface IShareManager {

	IDocumentSession createLocalShare(String docTitle, String initialContent, IRemoteMessageCallback operationCallback);
}
