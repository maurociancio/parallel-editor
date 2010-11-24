package ar.noxit.paralleleditor.eclipse.views.share;

import java.util.Collection;

public class DocumentElement {

	private String title;
	private Collection<String> users;

	public DocumentElement(String title, Collection<String> users) {
		this.title = title;
		this.users = users;
	}

	public String getTitle() {
		return title;
	}

	public Collection<String> getUsers() {
		return users;
	}
}