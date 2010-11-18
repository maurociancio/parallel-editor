package ar.noxit.paralleleditor.eclipse.infrastructure.share.manager;

public class DocumentAlreadySharedException extends RuntimeException {

	private final String docTitle;

	public DocumentAlreadySharedException(String message, String docTitle) {
		super(message);
		this.docTitle = docTitle;
	}

	public String getDocTitle() {
		return docTitle;
	}
}
