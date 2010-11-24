package ar.noxit.paralleleditor.eclipse.views.share;

import ar.noxit.paralleleditor.eclipse.share.sync.IDocumentSession;

public interface IRemoteDocumentShare {

	void shareRemoteDocument(String docTitle, String initialContent, IDocumentSession docSession);
}
