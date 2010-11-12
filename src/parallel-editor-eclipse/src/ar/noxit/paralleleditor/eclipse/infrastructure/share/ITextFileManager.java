package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

public interface ITextFileManager {

	void connect(IPath location, LocationKind locationKind, IProgressMonitor monitor) throws CoreException;
}
