/*
 *  A real-time collaborative tool to develop files over the network.
 *  Copyright (C) 2010  Mauro Ciancio and Leandro Gilioli
 *                      {maurociancio,legilioli} at gmail dot com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ar.noxit.paralleleditor.eclipse.editor;

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
		setTitle("Select a file to be opened for concurrent editing");
		setMessage("The remote file does not exist or it's in a non opened project in the current workspace.\n" +
				"The following files present in active projects can be opened for edition:");
		setMultipleSelection(false);
		setElements(fileList.toArray());
	}

	public IFile getSelectedFile() {
		return (IFile) ((getResult() != null) ? getResult()[0] : null);
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
