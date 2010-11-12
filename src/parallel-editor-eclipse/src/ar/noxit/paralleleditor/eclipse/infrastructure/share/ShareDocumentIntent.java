package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

public class ShareDocumentIntent extends AbstractShareDocumentIntent {

	protected ITextFileManager getTextFileBufferManager() {
		return new ITextFileManager() {

			@Override
			public void connect(IPath location, LocationKind locationKind, IProgressMonitor monitor)
					throws CoreException {
				ITextFileBufferManager.DEFAULT.connect(location, locationKind, monitor);
			}
		};
	}
}
