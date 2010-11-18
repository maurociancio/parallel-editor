package ar.noxit.paralleleditor.eclipse.infrastructure.share.manager;

import ar.noxit.paralleleditor.client.JSession;
import ar.noxit.paralleleditor.common.Message;
import ar.noxit.paralleleditor.common.converter.RemoteDocumentOperationConverter;
import ar.noxit.paralleleditor.common.operation.DocumentOperation;
import ar.noxit.paralleleditor.common.operation.EditOperation;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.IDocumentSession;

public class DocumentSession implements IDocumentSession {

	private final JSession session;
	private final String docTitle;
	private final RemoteDocumentOperationConverter converter;

	public DocumentSession(String docTitle, JSession localSession, RemoteDocumentOperationConverter converter) {
		this.docTitle = docTitle;
		this.converter = converter;
		this.session = localSession;
	}

	@Override
	public void onNewLocalMessage(Message<EditOperation> message) {
		session.send(converter.convert(new DocumentOperation(docTitle, message)));
	}
}