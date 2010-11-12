package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.IPath;

import ar.noxit.paralleleditor.eclipse.menu.actions.IShareDocumentIntent;

public class ShareDocumentIntent implements IShareDocumentIntent {

	@Override
	public void shareDocument(IPath fullPath, LocationKind locationKind) {
	}
}
