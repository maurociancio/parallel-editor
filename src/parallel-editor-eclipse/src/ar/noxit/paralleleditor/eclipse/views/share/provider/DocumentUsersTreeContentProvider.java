package ar.noxit.paralleleditor.eclipse.views.share.provider;

import java.util.Collection;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

import ar.noxit.paralleleditor.eclipse.views.share.DocumentElement;

public class DocumentUsersTreeContentProvider extends ArrayContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof DocumentElement) {
			return ((DocumentElement) parentElement).getUsers().toArray();
		} else {
			return null;
		}
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof DocumentElement) {
			Collection<String> users = ((DocumentElement) element).getUsers();
			return users != null && users.size() > 0;
		}
		return false;
	}
}