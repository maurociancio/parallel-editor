package ar.noxit.paralleleditor.eclipse.infrastructure.share;

public interface IShareManager {

	IDocumentSession createLocalShare(String docTitle, String initialContent, IRemoteMessageCallback operationCallback);
}
