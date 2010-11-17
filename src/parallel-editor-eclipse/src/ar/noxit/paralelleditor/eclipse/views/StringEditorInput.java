package ar.noxit.paralelleditor.eclipse.views;

import java.io.InputStream;
import java.io.StringBufferInputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

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
		return null;
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

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public IStorage getStorage() throws CoreException {
		return new IStorage() {

			@SuppressWarnings("deprecation")
			public InputStream getContents() throws CoreException {
				return new StringBufferInputStream(content);
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

			public Object getAdapter(Class adapter) {
				return null;
			}
		};
	}
}
