package ar.noxit.paralleleditor.eclipse.views;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.IDocumentSession;

public interface IRemoteDocumentShare {

	void shareRemoteDocument(String docTitle, String initialContent, IDocumentSession docSession);
}
