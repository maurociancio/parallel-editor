package ar.noxit.paralleleditor.eclipse.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class TreeLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		return null;
	}

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