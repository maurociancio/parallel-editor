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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

public class StringEditorInput implements IStorageEditorInput {

	private final String content;
	private final String title;

	public StringEditorInput(String title, String content) {
		super();
		this.title = title;
		this.content = content;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		try {
			return IDE.getEditorDescriptor(title).getImageDescriptor();
		} catch (PartInitException e) {
			return null;
		}
	}

	@Override
	public String getName() {
		return title;
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return title;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public IStorage getStorage() throws CoreException {
		return new IStorage() {

			public InputStream getContents() throws CoreException {
				try {
					return new ByteArrayInputStream(content.getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					throw new IllegalStateException("unsupported encoding", e);
				}
			}

			public IPath getFullPath() {
				return null;
			}

			public String getName() {
				return StringEditorInput.this.title;
			}

			public boolean isReadOnly() {
				return false;
			}

			@SuppressWarnings("rawtypes")
			public Object getAdapter(Class adapter) {
				return null;
			}
		};
	}
}
