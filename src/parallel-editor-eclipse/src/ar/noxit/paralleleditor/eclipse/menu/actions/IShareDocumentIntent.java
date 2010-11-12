package ar.noxit.paralleleditor.eclipse.menu.actions;

import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.IPath;

public interface IShareDocumentIntent {

	void shareDocument(IPath fullPath, LocationKind locationKind);
}
