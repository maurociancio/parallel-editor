package ar.noxit.paralleleditor.eclipse.share;

public class SubscriptionAlreadyExistsException extends RuntimeException {

	private String docTitle;

	public SubscriptionAlreadyExistsException(String message, String docTitle) {
		super(message);
		this.docTitle = docTitle;
	}

	public String getDocTitle() {
		return docTitle;
	}
}
