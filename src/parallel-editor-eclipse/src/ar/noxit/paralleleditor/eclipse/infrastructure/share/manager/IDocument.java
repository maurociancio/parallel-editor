package ar.noxit.paralleleditor.eclipse.infrastructure.share.manager;

import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.texteditor.ITextEditor;

public interface IDocument {

	IPath getFullPath();

	LocationKind getLocationKind();

	ITextEditor getTextEditor();
}
