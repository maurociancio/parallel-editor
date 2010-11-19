package ar.noxit.paralleleditor.eclipse.infrastructure.share.manager;

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
