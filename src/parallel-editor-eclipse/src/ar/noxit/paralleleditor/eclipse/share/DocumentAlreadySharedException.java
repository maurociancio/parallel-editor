package ar.noxit.paralleleditor.eclipse.share;

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
