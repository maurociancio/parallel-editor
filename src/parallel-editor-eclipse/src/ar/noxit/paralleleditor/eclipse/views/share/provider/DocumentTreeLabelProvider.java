package ar.noxit.paralleleditor.eclipse.views.share.provider;

import org.eclipse.jface.viewers.LabelProvider;

import ar.noxit.paralleleditor.eclipse.views.share.DocumentElement;

public class DocumentTreeLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof String)
			return (String) element;
		else {
			// document element
			return ((DocumentElement) element).getTitle();
		}
	}
}