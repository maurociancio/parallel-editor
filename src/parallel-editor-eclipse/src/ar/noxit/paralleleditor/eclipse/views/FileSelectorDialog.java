package ar.noxit.paralleleditor.eclipse.views;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public class FileSelectorDialog extends ElementListSelectionDialog {

	public FileSelectorDialog(Shell parent, Collection<IFile> fileList) {
		super(parent, new FileLabelProvider());
		setTitle("Select a file to be opened");
		setMessage("The specified project does not exist or is not active in the current workspace. \n The following files present in active projects can be opened for edition:");
		setMultipleSelection(false);
		setElements(fileList.toArray());
	}

	public IFile getSelectedFile(){
		return (IFile) ((getResult()!=null)?getResult()[0]:null);
	}
	
	private static class FileLabelProvider extends LabelProvider {

		private static final Image IMG_FILE = PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJ_FILE);

		public Image getImage(Object element) {
			if (element instanceof IFile)
				return IMG_FILE;
			else
				return null;
		}

		public String getText(Object element) {
			if (element instanceof IFile)
				return ((IFile) element).getFullPath().toOSString();
			else
				return "unknown element";
		}

	}

}
