package ar.noxit.paralleleditor.eclipse.views.share.provider;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

public class DocumentLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		try {
			return IDE.getEditorDescriptor(element.toString()).getImageDescriptor().createImage();
		} catch (PartInitException e) {
			return null;
		}
	}

	@Override
	public String getText(Object element) {
		return element.toString();
	}
}