package ar.noxit.paralleleditor.eclipse.infrastructure.share;

public interface IShareManager {

	IDocumentSession createShare(String docTitle, String initialContent, IOperationCallback operationCallback);
}
