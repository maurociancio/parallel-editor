package ar.noxit.paralleleditor.eclipse.infrastructure.share.manager;

import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.IPath;

public interface IDocument {

	IPath getFullPath();

	LocationKind getLocationKind();
}
